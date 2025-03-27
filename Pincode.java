import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Pincode {
    private static final String PIN = "1234";
    private static final int MAX_ATTEMPTS = 3;

    public static boolean authenticate() {
        int attempts = MAX_ATTEMPTS;
        while (attempts > 0) {
            String input = JOptionPane.showInputDialog(null, "🔒 Enter your 4-digit PIN:");
            if (input != null && input.equals(PIN)) {
                return true;
            } else {
                attempts--;
                JOptionPane.showMessageDialog(null, "❌ Invalid PIN. Attempts remaining: " + attempts);
            }
        }
        return false;
    }

    public static void main(String[] args) {
        if (authenticate()) {
            SwingUtilities.invokeLater(FinanceTracker::new); // Ensure class name matches your GUI class
        } else {
            JOptionPane.showMessageDialog(null, "🔐 Maximum attempts exceeded. Exiting application.");
            System.exit(0);
        }
    }
}
