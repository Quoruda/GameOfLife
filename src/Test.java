import javax.swing.*;
import java.awt.*;

public class Test extends JFrame {
    public Test() {
        // Configuration de la fenêtre principale
        setTitle("Exemple de JFrame avec JPanel et boutons");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Création du panneau pour les boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Ajout de boutons au panneau des boutons
        JButton button1 = new JButton("Bouton 1");
        JButton button2 = new JButton("Bouton 2");
        buttonPanel.add(button1);
        buttonPanel.add(button2);

        // Ajout du panneau des boutons en haut de la fenêtre
        add(buttonPanel, BorderLayout.NORTH);

        // Création du panneau pour le contenu général
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        // Ajout de contenu au panneau général (par exemple, un label)
        JLabel contentLabel = new JLabel("Contenu général", SwingConstants.CENTER);
        contentPanel.add(contentLabel, BorderLayout.CENTER);

        // Ajout du panneau de contenu au centre de la fenêtre
        add(contentPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Création et affichage de la fenêtre principale
        SwingUtilities.invokeLater(() -> {
            Test frame = new Test();
            frame.setVisible(true);
        });
    }
}

