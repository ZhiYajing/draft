/*
 * GUI foe displaying file item
 */

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class MyTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	public MyTreeCellRenderer(){
    }
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        JLabel label = (JLabel)super.getTreeCellRendererComponent(tree,value,selected,expanded,leaf,row,hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        MyFile myFile = (MyFile) node.getUserObject();
        label.setText(myFile.getName());
        label.setIcon(new ImageIcon());
        label.setOpaque(false);
        return label;
    }

    @Override
    public void setBackground(Color bg) {
        if (bg instanceof ColorUIResource){
            bg = null;
        }
        super.setBackground(bg);
    }
}