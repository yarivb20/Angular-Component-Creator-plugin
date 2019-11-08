package outbrain.component;

import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class ComponentCreatorDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField componentNameTextField;
    private JComboBox<String> modulesComboBox1;
    private JCheckBox stateCheckBox;

    private boolean hasCanceled = false;

    public ComponentCreatorDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    public Map<String, Object> getTemplateVars() {
        Map<String, Object> templateModel = new HashMap<String, Object>();
        templateModel.put("componentName", getComponentName());
        templateModel.put("modelFile", getSelectedModelFile());
        templateModel.put("state", getState());
        return templateModel;
    }

    public void setModelFilesList(String[] items) {
        for(String item: items){
            modulesComboBox1.addItem(item);
        }
    }

    public String getComponentName() {
        return componentNameTextField.getText();
    }

    public String getSelectedModelFile() {
        return String.valueOf(modulesComboBox1.getSelectedItem());
    }

    public boolean getState() {
        return stateCheckBox.isSelected();
    }

    public boolean isCanceled() {
        return hasCanceled;
    }

    private void onOK() {
        hasCanceled = false;
        dispose();
    }

    private void onCancel() {
        hasCanceled = true;
        dispose();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
