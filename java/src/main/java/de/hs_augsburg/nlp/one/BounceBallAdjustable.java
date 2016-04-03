package de.hs_augsburg.nlp.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.vecmath.Vector2d;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.round;

public class BounceBallAdjustable {
    // Atomic reference of a Immutable Persistent HashMap of Balls
    private final ConcurrentHashMap<Integer, Ball> allBalls = new ConcurrentHashMap<>();
    private final Box box;
    private java.util.List<Thread> moversThreads = new LinkedList<>();
    private int currentBallIndex = 1;
    private Logger logger;

    public BounceBallAdjustable() {
        JFrame frame = new JFrame("Bounce threaded");
        box = new Box();
        frame.add("Center", box);
        JPanel p = new JPanel();
        JButton b;
        p.add(b = new JButton("Start"));
        b.addActionListener(new CommandListener(CommandListener.NEWBALL));
        p.add(b = new JButton("Faster"));
        b.addActionListener(new CommandListener(CommandListener.FASTER));
        p.add(b = new JButton("Slower"));
        b.addActionListener(new CommandListener(CommandListener.SLOWER));
        p.add(b = new JButton("Stop"));
        b.addActionListener(new CommandListener(CommandListener.STOP));
        p.add(b = new JButton("Close"));
        b.addActionListener(new CommandListener(CommandListener.EXIT));
        frame.add("South", p);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setVisible(true);
        logger = LoggerFactory.getLogger(this.getClass());
    }

    public static void main(String[] args) {
        new BounceBallAdjustable();
    }

    protected void makeAndStartBall() {
        Ball b = new Ball(Color.black);
        allBalls.put(currentBallIndex, b);
        BallMover mover = new BallMover(currentBallIndex, allBalls);
        Thread thread = new Thread(mover);
        thread.start();
        moversThreads.add(thread);
        currentBallIndex += 1;
    }

    void stopAllMovementAndProcessing() {
        for (Thread moverThread : moversThreads) {
            moverThread.interrupt();
        }
    }

    class Box extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintBalls(g);
        }

        private void paintBalls(Graphics g) {
            for (Ball ball : allBalls.values()) {
                ball.draw(g);
            }
        }
    }

    class CommandListener implements ActionListener {
        static final int EXIT = 0;
        static final int NEWBALL = 1;
        static final int FASTER = 2;
        static final int SLOWER = 3;
        static final int STOP = 4;
        private int command; // der Listener merkt sich seine Aufgabe

        public CommandListener(int command) {
            this.command = command;
        }

        public void actionPerformed(ActionEvent e) {
            switch (command) {
                case EXIT:
                    System.exit(0);
                    break;
                case FASTER:
                    allBalls.replaceAll((k, v) -> v.adjustSpeed(2));
                    break;
                case SLOWER:
                    allBalls.replaceAll((k, v) -> v.adjustSpeed(-1));
                    break;
                case STOP:
                    stopAllMovementAndProcessing();
                    box.repaint();
                    break;
                case NEWBALL:
                    makeAndStartBall();
                    break;
            }
        }
    }

    class BallMover implements Runnable {

        private int ballIdentity;
        private Map<Integer, Ball> ballAtom;

        public BallMover(int ballIdentity, Map<Integer, Ball> ballAtom) {
            this.ballIdentity = ballIdentity;
            this.ballAtom = ballAtom;
        }

        @Override
        public void run() {
            try {
                for (int i = 1; i <= 1000; i++) {
                    if (Thread.currentThread().isInterrupted()){
                        return;
                    }
                    // potential for a race condition if the thread is interrupted here
                    ballAtom.compute(ballIdentity, (k, v) -> v.move());
                    Thread.sleep(5); // cpu-schonendes Warten
                    box.repaint();
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.debug("Runable Interupted", e);
                logger.info("Runable Interupted");
                Thread.currentThread().interrupt();
            }
            // allBalls.remove(this); // ball leaves animation quietly
        }
    }

    class Ball {
        private static final int XSIZE = 10; // Ballgroesse
        private static final int YSIZE = 10;
        private final Color color; // die Ballfarbe
        private final int x; // momentane Ballposition
        private final int y;
        private final int dx; // Translation pro Zeiteinheit
        private final int dy;

        public Ball(Color co) {
            this(co, 0, 0, 2, 2);
        }

        public Ball(Color color, int x, int y, int dx, int dy) {
            this.color = color;
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
        }

        public void draw(Graphics g) {
            g.setColor(color);
            g.fillOval(x, y, XSIZE, YSIZE);
        }

        public Ball move() {
            // ein Stueck fortbewegen
            int newX = x;
            int newY = y;
            int newDx = dx;
            int newDy = dy;
            newX += dx;
            newY += dy;

            // bei Anstossen "umdrehen"
            Dimension d = box.getSize();
            if (newX < 0) {
                newX = 0;
                newDx = -newDx;
            }
            if (newX + XSIZE >= d.width) {
                newX = d.width - XSIZE;
                newDx = -newDx;
            }
            if (newY < 0) {
                newY = 0;
                newDy = -newDy;
            }
            if (newY + YSIZE >= d.height) {
                newY = d.height - YSIZE;
                newDy = -newDy;
            }
            return new Ball(this.color, newX, newY, newDx, newDy);
        }

        public Ball adjustSpeed(int deltaSpeed) {
            Vector2d oldSpeed = new Vector2d(new double[]{dx, dy});
            Vector2d newSpeed = new Vector2d(new double[]{dx, dy});
            newSpeed.normalize();
            newSpeed.scaleAdd(deltaSpeed, oldSpeed);

            return new Ball(this.color, this.x, this.y, (int) round(newSpeed.x), (int) round(newSpeed.y));
        }

    }
}
