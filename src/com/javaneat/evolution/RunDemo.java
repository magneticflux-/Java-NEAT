package com.javaneat.evolution;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Output;
import com.grapeshot.halfnes.CPURAM;
import com.grapeshot.halfnes.NES;
import com.grapeshot.halfnes.ui.HeadlessUI;
import com.grapeshot.halfnes.ui.PuppetController;
import com.javaneat.genome.ConnectionGene;
import com.javaneat.genome.NEATGenome;
import com.javaneat.phenome.NEATPhenome;

import org.apache.commons.math3.util.FastMath;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.UserAbort;
import org.uncommons.watchmaker.swing.ObjectSwingRenderer;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class RunDemo {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        SelectionStrategy<Object> selectionStrategy = new TournamentSelection(Probability.ONE);
        final Kryo kryo = new Kryo();
        Random rng = new Random(0);

        final int numInputs = 169;
        final int numOutputs = 6;
        final int populationSize = 300;
        final double eliteFraction = 0.1;
        final double disjointGeneCoefficient = 2;
        final double excessGeneCoefficient = 2;
        final double weightDifferenceCoefficient = 1;
        final int speciesTarget = 20;
        final int speciesStagnantTimeLimit = 500;
        final double speciesCutoff = 5;
        final double speciesCutoffDelta = 0.5;
        final double enableMutationProb = 0.2;
        final double disableMutationProb = 0.4;
        final double mutationWeightWholeProb = 0.25;
        final double mutationWeightProb = 0.9;
        final double mutationAddLinkProb = 0.3;
        final double mutationRemoveLinkProb = 0.3;
        final double mutationAddNodeProb = 0.3;
        final double mutationWeightRange = 0.1;
        final double crossoverChance = 0.75;
        NEATGenomeManager manager = new NEATGenomeManager(numInputs, numOutputs, disjointGeneCoefficient, excessGeneCoefficient, weightDifferenceCoefficient,
                speciesTarget, speciesCutoff, speciesCutoffDelta, populationSize, speciesStagnantTimeLimit, mutationWeightWholeProb, mutationWeightProb,
                mutationAddLinkProb, mutationAddNodeProb, mutationWeightRange, enableMutationProb, disableMutationProb, crossoverChance, mutationRemoveLinkProb);

        CandidateFactory<NEATGenome> candidateFactory = new NEATGenotypeFactory(manager);
        EvolutionaryOperator<NEATGenome> evolutionScheme = new NEATEvolutionaryOperator(manager);
        FitnessEvaluator<NEATGenome> fitnessEvaluator = new FitnessEvaluator<NEATGenome>() {
            public double getFitness(NEATGenome candidate, List<? extends NEATGenome> population) {
                double error = 0;

                NEATPhenome network = new NEATPhenome(candidate);

                error += FastMath.abs(0 - network.stepTime(new double[]{0, 0}, 4)[0]);
                error += FastMath.abs(1 - network.stepTime(new double[]{0, 1}, 4)[0]);
                error += FastMath.abs(1 - network.stepTime(new double[]{1, 0}, 4)[0]);
                error += FastMath.abs(0 - network.stepTime(new double[]{1, 1}, 4)[0]);

                candidate.setScore(1 / (1 + error + candidate.getConnectionGeneList().size()));
                return candidate.getAdjustedScore();
            }

            public boolean isNatural() {
                return true;
            }
        };

        fitnessEvaluator = new NESFitness();

        GenerationalEvolutionEngine<NEATGenome> ge = new GenerationalEvolutionEngine<>(candidateFactory, evolutionScheme, fitnessEvaluator,
                selectionStrategy, rng);
        ge.addEvolutionObserver(new EvolutionObserver<NEATGenome>() {
            private long startTime = System.nanoTime();

            public void populationUpdate(PopulationData<? extends NEATGenome> data) {
                try (Output output = new Output(new FileOutputStream("NEAT-Mario/" + "generation_" + data.getGenerationNumber() + ".pop"));) {
                    kryo.writeClassAndObject(output, data.getBestCandidate());
                } catch (KryoException | FileNotFoundException e) {
                    e.printStackTrace();
                }

                System.out.printf("Generation %d: %s\n", data.getGenerationNumber(), data.getBestCandidate());
                System.out.println("Max fitness: " + data.getBestCandidateFitness());
                System.out.println("Time taken: " + (System.nanoTime() - startTime) / 1000000000f + " seconds");
                startTime = System.nanoTime();
                if (!Double.isFinite(data.getBestCandidateFitness())) {
                    System.err.println("Fitness was infinite.");
                    System.err.println("Genome: " + data.getBestCandidate());
                    System.err.println("Species: " + data.getBestCandidate().getSpecies());
                    System.exit(0);
                }
            }
        });

        final UserAbort abort = new UserAbort();
        final EvolutionMonitor<NEATGenome> monitor = new EvolutionMonitor<>(new ObjectSwingRenderer(), false);
        synchronized (monitor.getGUIComponent().getTreeLock()) {
            ((JTabbedPane) monitor.getGUIComponent().getComponents()[0]).add(new JPanel() {
                private static final long serialVersionUID = 1L;

                {
                    this.setName("Abort Button");
                    this.setLayout(new BorderLayout());
                    this.add(new JButton("ABORT") {
                        private static final long serialVersionUID = 1L;

                        {
                            this.setBackground(Color.RED);
                            this.setMaximumSize(new Dimension(100, 50));
                            this.setPreferredSize(new Dimension(100, 50));

                            this.addActionListener(e -> {
                                abort.abort();
                                System.out.println("*** ABORT SEQUENCE ACTIVATED ***");
                            });
                        }
                    }, BorderLayout.PAGE_START);
                }
            });
        }
        monitor.showInFrame("Evolution", true);
        ge.addEvolutionObserver(monitor);

        final NEATGenome result = ge.evolve(populationSize, (int) FastMath.round(populationSize * eliteFraction), abort);
        System.out.println("Fittest individual: " + result);
        new NEATPhenome(result);
    }
}

class NESFitness implements FitnessEvaluator<NEATGenome> {
    private ThreadLocal<NES> nes = new ThreadLocal<>();
    private ThreadLocal<HeadlessUI> ui = new ThreadLocal<>();

    public static double[] unwind2DArray(int[][] arr) {
        double[] out = new double[arr.length * arr[0].length];
        int i = 0;
        for (int x = 0; x < arr[0].length; x++)
            for (int[] anArr : arr) {
                out[i] = anArr[x];
                i++;
            }
        return out;
    }


    @SuppressWarnings("MagicNumber")
    @Override
    public double getFitness(NEATGenome candidate, List<? extends NEATGenome> population) {
        // long startTime = System.nanoTime();

        boolean movesRight = false; // Throw out ones that don't move right before testing
        for (ConnectionGene gene : candidate.getConnectionGeneList()) {
            if (gene.getToNode() == candidate.getManager().getOutputOffset() + 3) {
                movesRight = true;
                break;
            }
        }
        if (!movesRight) {
            candidate.setScore(500 - candidate.getConnectionGeneList().size() * 10);
            return candidate.getScore();
        }

        NEATPhenome network = new NEATPhenome(candidate);
        if (nes.get() == null || ui.get() == null) {
            ui.set(new HeadlessUI("C:\\Users\\Mitchell\\Desktop\\fceux-2.2.2-win32\\ROMs\\Super Mario Bros..nes", false));
            nes.set(new NES(ui.get()));
        }
        NES nes = this.nes.get();
        HeadlessUI ui = this.ui.get();
        nes.reset();

        CPURAM cpuram;
        PuppetController controller1 = (PuppetController) nes.getcontroller1();

        for (int i = 0; i < 31; i++)
            // Exact frame number until it can begin.
            ui.runFrame();

        controller1.pressButton(PuppetController.Button.START);
        ui.runFrame();
        controller1.releaseButton(PuppetController.Button.START);

        for (int i = 0; i < 162; i++)
            // Exact frame number until Mario gains control
            ui.runFrame();

        int fitness;
        int maxDistance = 0;
        int timeout = 0;

        while (true) {
            controller1.resetButtons();

            cpuram = ui.getNESCPURAM();

            int score = 0;
            int time = 0;
            byte world = (byte) cpuram.read(0x075F);
            byte level = (byte) cpuram.read(0x0760);
            byte lives = (byte) (cpuram.read(0x075A) + 1);
            int marioX = cpuram.read(0x6D) * 0x100 + cpuram.read(0x86);
            int marioY = cpuram.read(0x03B8) + 16;
            int marioState = cpuram.read(0x000E);
            for (int i = 0x07DD; i <= 0x07E2; i++)
                score += cpuram._read(i) * FastMath.pow(10, (0x07E2 - i + 1));
            for (int i = 0x07F8; i <= 0x07FA; i++)
                time += cpuram._read(i) * FastMath.pow(10, (0x07FA - i));

            int points = (score / 5) + (time * 10) + (marioX / 4) + (lives * 500) + (level * 250) + (world * 2000);

            timeout++;
            if (marioX > maxDistance) {
                maxDistance = marioX;
                timeout = 0;
            }
            // System.out.println("Lives: " + lives + " Timeout: " + timeout + " Distance: " + marioX);
            if (lives <= 2 || timeout > 60 || marioState == 0x0B) {
                fitness = points;
                break;
            }

            final int[][] vision = new int[13][13];

            for (int dx = -vision[0].length / 2; dx < vision[0].length / 2; dx += 1)
                for (int dy = -vision.length / 2; dy < vision.length / 2; dy += 1) {
                    int x = marioX + (dx * 16) + 8;
                    int y = marioY + (dy * 16) - 16;
                    int page = (int) FastMath.floor(x / 256) % 2;
                    int subx = (int) FastMath.floor((x % 256) / 16);
                    int suby = (int) FastMath.floor((y - 32) / 16);
                    int addr = 0x500 + page * 13 * 16 + suby * 16 + subx;
                    if (suby >= 13 || suby < 0) {
                        // System.out.println("Outside level.");
                        vision[dy + (vision.length / 2)][dx + (vision[0].length / 2)] = 0;
                    } else {
                        // System.out.println("Block data at " + dx + ", " + dy + ": " + nes.cpuram.read(addr));
                        vision[dy + (vision.length / 2)][dx + (vision[0].length / 2)] = cpuram.read(addr);
                    }
                }

            for (int i = 0; i <= 4; i++) {
                int enemy = cpuram.read(0xF + i);
                if (enemy != 0) {
                    int ex = cpuram.read(0x6E + i) * 0x100 + cpuram.read(0x87 + i);
                    int ey = cpuram.read(0xCF + i) + 24;
                    int enemyMarioDeltaX = (ex - marioX) / 16;
                    int enemyMarioDeltaY = (ey - marioY) / 16;
                    try {
                        vision[enemyMarioDeltaY + (vision.length / 2)][enemyMarioDeltaX + (vision[0].length / 2)] = -enemy;
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }
                }
            }

            double[] visionUnwound = NESFitness.unwind2DArray(vision);
            double[] reactions = network.stepTime(visionUnwound);

            if (reactions[0] > 0) controller1.pressButton(PuppetController.Button.UP);
            if (reactions[1] > 0) controller1.pressButton(PuppetController.Button.DOWN);
            if (reactions[2] > 0) controller1.pressButton(PuppetController.Button.LEFT);
            if (reactions[3] > 0) controller1.pressButton(PuppetController.Button.RIGHT);
            if (reactions[4] > 0) controller1.pressButton(PuppetController.Button.A);
            if (reactions[5] > 0) controller1.pressButton(PuppetController.Button.B);
            // if (reactions[6] > 0) input.keyPressed(SELECT);
            // if (reactions[7] > 0) input.keyPressed(START);

            nes.frameAdvance();
        }
        fitness -= candidate.getConnectionGeneList().size() * 5;
        // fitness -= 5400; // The approximate minimum
        // System.out.println("Finished one evaluation that took " + (System.nanoTime() - startTime) / 1000000000f + " seconds.");
        fitness = fitness >= 0 ? fitness : 0;

        candidate.setScore(fitness);
        return candidate.getScore();
    }

    @Override
    public boolean isNatural() {
        return true;
    }
}
