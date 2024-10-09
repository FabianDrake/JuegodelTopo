import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class JuegoDelTopo extends JPanel implements MouseListener {
    private Image topoImage;
    private Image mazoImage;
    private int topoX, topoY;
    private int destinoX, destinoY;
    private int topoWidth = 100, topoHeight = 100;
    private int golpes;
    private int[][] hoyos = new int[6][2]; // Coordenadas de los 6 hoyos
    private Random random = new Random();
    private int intentos = 0; // Contador de intentos
    private Timer timer;
    private final int maxIntentos = 10;
    private int stepSize = 10; // Tamaño del paso de traslación
    private boolean juegoIniciado = false;
    private JButton botonIniciar;

    public JuegoDelTopo(JButton botonIniciar) {
        this.botonIniciar = botonIniciar;
        cargarImagenes();
        configurarHoyos();
        setPreferredSize(new Dimension(800, 600));
        addMouseListener(this);

        // Cambiar el cursor a la imagen del mazo
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Cursor mazoCursor = toolkit.createCustomCursor(mazoImage, new Point(0, 0), "Mazo");
        setCursor(mazoCursor);

        // Configurar el temporizador para la animación
        timer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (juegoIniciado) {
                    animarTraslacion();
                }
            }
        });
    }

    // Cargar imágenes del topo y mazo
    private void cargarImagenes() {
        topoImage = new ImageIcon("Imagenes/topo.png").getImage();
        mazoImage = new ImageIcon("Imagenes/mazo.png").getImage();
    }

    // Configurar coordenadas para los hoyos
    private void configurarHoyos() {
        hoyos[0][0] = 150; hoyos[0][1] = 200;
        hoyos[1][0] = 350; hoyos[1][1] = 200;
        hoyos[2][0] = 550; hoyos[2][1] = 200;
        hoyos[3][0] = 150; hoyos[3][1] = 400;
        hoyos[4][0] = 350; hoyos[4][1] = 400;
        hoyos[5][0] = 550; hoyos[5][1] = 400;
    }

    // Mover el topo a un hoyo aleatorio
    private void moverTopo() {
        if (intentos < maxIntentos) {
            int index = random.nextInt(hoyos.length);
            destinoX = hoyos[index][0];
            destinoY = hoyos[index][1];
            intentos++;
        } else {
            mostrarMensajeFinJuego();
        }
    }

    // Animar la traslación del topo
    private void animarTraslacion() {
        // Movimiento horizontal
        if (topoX < destinoX) {
            topoX = Math.min(topoX + stepSize, destinoX);
        } else if (topoX > destinoX) {
            topoX = Math.max(topoX - stepSize, destinoX);
        }

        // Movimiento vertical
        if (topoY < destinoY) {
            topoY = Math.min(topoY + stepSize, destinoY);
        } else if (topoY > destinoY) {
            topoY = Math.max(topoY - stepSize, destinoY);
        }

        // Si el topo llega a la posición destino, mover a otro hoyo
        if (topoX == destinoX && topoY == destinoY) {
            moverTopo();
        }

        repaint();
    }

    // Dibujar fondo, hoyos y topo
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Fondo con degradado
        GradientPaint gradient = new GradientPaint(0, 0, Color.CYAN, 0, getHeight(), Color.GREEN);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Dibujar hoyos
        g.setColor(Color.DARK_GRAY);
        for (int[] hoyo : hoyos) {
            g.fillOval(hoyo[0], hoyo[1], 120, 80);
        }

        // Dibujar topo si el juego está activo
        if (juegoIniciado && intentos <= maxIntentos) {
            g.drawImage(topoImage, topoX, topoY, topoWidth, topoHeight, this);
        }

        // Mostrar cantidad de golpes e intentos restantes
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Golpes: " + golpes, 50, 50);
        g.drawString("Intentos restantes: " + (maxIntentos - intentos), 50, 80);
    }

    // Evento de clic para detectar si se golpea al topo
    @Override
    public void mouseClicked(MouseEvent e) {
        if (juegoIniciado && intentos <= maxIntentos) {
            int mouseX = e.getX();
            int mouseY = e.getY();

            // Verificar si el clic fue sobre el topo
            if (mouseX >= topoX && mouseX <= topoX + topoWidth &&
                    mouseY >= topoY && mouseY <= topoY + topoHeight) {
                golpes++;
                if (golpes == 3) {
                    mostrarMensajeGanador();
                }
                moverTopo();
            }
        }
    }

    // Mensaje cuando se gana
    private void mostrarMensajeGanador() {
        JOptionPane.showMessageDialog(this, "¡Has ganado el juego!");
        terminarJuego();
    }

    // Mensaje cuando se terminan los intentos
    private void mostrarMensajeFinJuego() {
        JOptionPane.showMessageDialog(this, "Juego terminado. Golpes acertados: " + golpes);
        terminarJuego();
    }

    // Terminar el juego y reiniciar variables
    private void terminarJuego() {
        juegoIniciado = false;
        timer.stop();
        botonIniciar.setEnabled(true); // Habilitar el botón de nuevo
    }

    // Iniciar el juego
    public void iniciarJuego() {
        juegoIniciado = true;
        intentos = 0;
        golpes = 0;
        moverTopo(); // Mover el topo inicialmente
        timer.start(); // Iniciar el temporizador para la animación
    }

    // Crear ventana principal
    public static void main(String[] args) {
        JFrame ventana = new JFrame("Juego del Topo - 22110092");
        JButton botonIniciar = new JButton("Iniciar Juego");
        JuegoDelTopo juego = new JuegoDelTopo(botonIniciar);

        ventana.setLayout(new BorderLayout());

        // Botón para iniciar el juego
        botonIniciar.setFont(new Font("Arial", Font.BOLD, 24));
        botonIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                juego.iniciarJuego();
                botonIniciar.setEnabled(false); // Deshabilitar el botón una vez que el juego ha iniciado
            }
        });

        ventana.add(juego, BorderLayout.CENTER);
        ventana.add(botonIniciar, BorderLayout.SOUTH);
        ventana.pack();
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Centrar la ventana
        ventana.setLocationRelativeTo(null);

        ventana.setVisible(true);
    }

    // Métodos no utilizados, pero requeridos por la interfaz MouseListener
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
