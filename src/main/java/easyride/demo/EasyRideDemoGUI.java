
package easyride.demo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

import java.awt.*;
import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class EasyRideDemoGUI extends JFrame {

    private final JLabel totalLabel = new JLabel("Total: 0");
    private final JLabel passedLabel = new JLabel("Passed: 0");
    private final JLabel failedLabel = new JLabel("Failed: 0");
    private final JLabel skippedLabel = new JLabel("Skipped: 0");

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new String[]{"Test Class", "Test Method", "Status", "Time"},
                    0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable testTable = new JTable(tableModel);

    private final Path reportDirectory =
            Paths.get("target", "surefire-reports");

    public EasyRideDemoGUI() {

        setTitle("EasyRide Dispatch Lab - Visual Testing Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        root.add(createHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Test Dashboard", createTestPanel());
        tabs.addTab("Module Overview", createModulePanel());
        tabs.addTab("Trip Lifecycle", createTripPanel());

        root.add(tabs, BorderLayout.CENTER);

        add(root);

        loadTestResults();
    }

    private JPanel createHeader() {

        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("EasyRide Dispatch Lab");
        title.setFont(new Font("Arial", Font.BOLD, 27));

        JLabel subtitle = new JLabel(
                "Visual Testing and Project Demonstration"
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 3, 3));
        textPanel.add(title);
        textPanel.add(subtitle);

        panel.add(textPanel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createTestPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 15));

        JPanel summary = new JPanel(new GridLayout(1, 4, 10, 10));

        summary.add(createSummaryCard("TOTAL TESTS", totalLabel));
        summary.add(createSummaryCard("PASSED", passedLabel));
        summary.add(createSummaryCard("FAILED", failedLabel));
        summary.add(createSummaryCard("SKIPPED", skippedLabel));

        testTable.setRowHeight(28);
        testTable.setFont(new Font("Arial", Font.PLAIN, 13));
        testTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        JScrollPane scrollPane = new JScrollPane(testTable);

        JButton refreshButton = new JButton("Refresh Test Results");
        refreshButton.addActionListener(e -> loadTestResults());

        JButton infoButton = new JButton("How to Run Tests");
        infoButton.addActionListener(e -> showRunInstructions());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(infoButton);
        buttons.add(refreshButton);

        panel.add(summary, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSummaryCard(String heading, JLabel value) {

        JPanel card = new JPanel(new BorderLayout(5, 8));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));

        JLabel title = new JLabel(heading, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 12));

        value.setHorizontalAlignment(SwingConstants.CENTER);
        value.setFont(new Font("Arial", Font.BOLD, 25));

        card.add(title, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    private JPanel createModulePanel() {

        JPanel panel = new JPanel(new GridLayout(0, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(createModuleCard(
                "1. Domain Module",
                "Customer, Driver, User, GeoPoint, Money, Trip and VehicleType."
        ));

        panel.add(createModuleCard(
                "2. Fare Calculation",
                "Base Fare, Surge Fare Decorator and Discount Fare Decorator."
        ));

        panel.add(createModuleCard(
                "3. Driver Matching",
                "Nearest Driver Matching and Highest Rated Driver Matching."
        ));

        panel.add(createModuleCard(
                "4. Trip Management",
                "Trip status, driver assignment, trip start, completion and cancellation."
        ));

        panel.add(createModuleCard(
                "5. Database",
                "SQLite database connection and customer, driver and trip persistence."
        ));

        panel.add(createModuleCard(
                "6. Automated Testing",
                "JUnit test cases for implemented project functionalities."
        ));

        return panel;
    }

    private JPanel createModuleCard(String title, String description) {

        JPanel card = new JPanel(new BorderLayout(8, 8));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Arial", Font.BOLD, 16));

        JTextArea details = new JTextArea(description);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        details.setEditable(false);
        details.setOpaque(false);
        details.setFont(new Font("Arial", Font.PLAIN, 13));

        card.add(heading, BorderLayout.NORTH);
        card.add(details, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTripPanel() {

        JPanel panel = new JPanel(new GridBagLayout());

        JPanel flow = new JPanel();
        flow.setLayout(new BoxLayout(flow, BoxLayout.Y_AXIS));
        flow.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        String[] states = {
                "REQUESTED",
                "ACCEPTED",
                "IN_PROGRESS",
                "COMPLETED"
        };

        for (int i = 0; i < states.length; i++) {

            JLabel state = new JLabel(states[i], SwingConstants.CENTER);
            state.setAlignmentX(Component.CENTER_ALIGNMENT);
            state.setOpaque(true);
            state.setBackground(new Color(225, 240, 255));
            state.setForeground(new Color(25, 65, 110));
            state.setFont(new Font("Arial", Font.BOLD, 17));
            state.setBorder(BorderFactory.createEmptyBorder(12, 35, 12, 35));

            flow.add(state);

            if (i < states.length - 1) {
                JLabel arrow = new JLabel("↓");
                arrow.setAlignmentX(Component.CENTER_ALIGNMENT);
                arrow.setFont(new Font("Arial", Font.BOLD, 25));
                flow.add(arrow);
            }
        }

        JLabel note = new JLabel(
                "A trip can also be cancelled before completion.",
                SwingConstants.CENTER
        );

        note.setFont(new Font("Arial", Font.ITALIC, 13));

        JPanel content = new JPanel(new BorderLayout(10, 15));
        content.add(flow, BorderLayout.CENTER);
        content.add(note, BorderLayout.SOUTH);

        panel.add(content);

        return panel;
    }

    private void loadTestResults() {

        tableModel.setRowCount(0);

        int total = 0;
        int passed = 0;
        int failed = 0;
        int skipped = 0;

        if (!Files.isDirectory(reportDirectory)) {

            updateSummary(0, 0, 0, 0);

            JOptionPane.showMessageDialog(
                    this,
                    "No test reports found.\n"
                            + "Run Maven tests first.",
                    "Test Reports Not Found",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        try (Stream<Path> files = Files.list(reportDirectory)) {

            List<Path> reportFiles = files
                    .filter(p -> p.getFileName().toString().startsWith("TEST-"))
                    .filter(p -> p.getFileName().toString().endsWith(".xml"))
                    .sorted()
                    .toList();

            for (Path report : reportFiles) {

                DocumentBuilderFactory factory =
                        DocumentBuilderFactory.newInstance();

                factory.setFeature(
                        "http://apache.org/xml/features/disallow-doctype-decl",
                        true
                );

                factory.setFeature(
                        "http://xml.org/sax/features/external-general-entities",
                        false
                );

                factory.setFeature(
                        "http://xml.org/sax/features/external-parameter-entities",
                        false
                );

                Document document = factory.newDocumentBuilder().parse(
                        report.toFile()
                );

                NodeList testCases =
                        document.getElementsByTagName("testcase");

                for (int i = 0; i < testCases.getLength(); i++) {

                    Element testCase = (Element) testCases.item(i);

                    String className =
                            testCase.getAttribute("classname");

                    String methodName =
                            testCase.getAttribute("name");

                    String time = testCase.getAttribute("time");

                    String status = "PASSED";

                    if (testCase.getElementsByTagName("failure").getLength() > 0
                            || testCase.getElementsByTagName("error").getLength() > 0) {

                        status = "FAILED";
                        failed++;

                    } else if (
                            testCase.getElementsByTagName("skipped").getLength() > 0
                    ) {

                        status = "SKIPPED";
                        skipped++;

                    } else {

                        passed++;
                    }

                    total++;

                    String shortClassName = className.substring(
                            className.lastIndexOf('.') + 1
                    );

                    tableModel.addRow(new Object[]{
                            shortClassName,
                            methodName,
                            status,
                            time + " sec"
                    });
                }
            }

            updateSummary(total, passed, failed, skipped);

            if (total == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No test cases found in the reports.",
                        "Empty Reports",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not read test reports:\n" + ex.getMessage(),
                    "Report Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateSummary(
            int total, int passed, int failed, int skipped
    ) {

        totalLabel.setText(String.valueOf(total));
        passedLabel.setText(String.valueOf(passed));
        failedLabel.setText(String.valueOf(failed));
        skippedLabel.setText(String.valueOf(skipped));
    }

    private void showRunInstructions() {

        JOptionPane.showMessageDialog(
                this,
                "1. Open PowerShell in the project root folder.\n\n"
                        + "2. Run: mvn clean test\n\n"
                        + "3. Wait for BUILD SUCCESS.\n\n"
                        + "4. Open this dashboard and click Refresh Test Results.\n\n"
                        + "The dashboard reads actual JUnit reports from "
                        + "target/surefire-reports.",
                "How to Run Tests",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            EasyRideDemoGUI gui = new EasyRideDemoGUI();
            gui.setVisible(true);
        });
    }
}
