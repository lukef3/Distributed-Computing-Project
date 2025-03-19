import javax.swing.*;

public class APPTEST {
    public static void main(String[] args) {

        new Thread(() -> {
            Server.main(new String[]{});
        }).start();

        int numberOfClients = 2;
        for (int i = 0; i < numberOfClients; i++) {
            JFrame frame = new JFrame("Client " + (i + 1));
            frame.setContentPane(new LoginForm().getPanel());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }
}
