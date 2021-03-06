package org.hkfree.topoagent.module;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import org.hkfree.topoagent.Language;
import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.core.LogService;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.view.About;
import org.hkfree.topoagent.view.Status;

/**
 *
 * @author Filip Valenta
 */
public class TrayService {
    
    private Core core;
    private TrayIcon trayIcon;
    
    private BufferedImage imgOk = null;
    private BufferedImage imgWarning = null;
    private BufferedImage imgError = null;
    
    public TrayService(Core core) {
        this.core = core;
        try {
            initSystemTray();
        } catch (AWTException ex) {
            LogService.Log2ConsoleError(this, ex);
        }
    }

    /**
     * Show some message
     *
     * example: "Tester!", "Some action performed", TrayIcon.MessageType.INFO
     *
     * @param title
     * @param content
     * @param type
     */
    public void showMessage(String title, String content, TrayIcon.MessageType type) {
        trayIcon.displayMessage(title, content, type);
        trayIcon.setToolTip(Language.APP_NAME + " - " + content);
    }
    
    private void initSystemTray() throws AWTException {
        if (!SystemTray.isSupported()) {
            LogService.Log2Console(this, "SystemTray is not supported");
        }
        else {
            final PopupMenu popup = new PopupMenu();
            
            try {
                imgOk = ImageIO.read(new File("icon-16x16.png"));
                imgWarning = ImageIO.read(new File("icon-16x16-warning.png"));
                imgError = ImageIO.read(new File("icon-16x16-error.png"));
            } catch (IOException e) {
                LogService.Log2Console(this, "SystemTray error reading image file");
            }
            
            trayIcon = new TrayIcon(imgOk, Language.APP_NAME);
            SystemTray tray = SystemTray.getSystemTray();
            
            MenuItem itemAbout = new MenuItem(Language.TITLE_ABOUT);
            itemAbout.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showWindowAbout(Language.TITLE_ABOUT);
                }
            });
            popup.add(itemAbout);
            
            MenuItem itemStatus = new MenuItem(Language.TITLE_STATUS);
            itemStatus.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showWindowStatus(Language.TITLE_STATUS);
                }
            });
            popup.add(itemStatus);
            
            popup.addSeparator();
            for (Probe p : core.getProbeLoader().getProbes()) {
                CheckboxMenuItem cb = new CheckboxMenuItem(p.getModuleName());
                cb.setState(true);
                cb.setEnabled(false);
                popup.add(cb);
            }
            popup.addSeparator();
            
            MenuItem exitItem = new MenuItem("Vypnout do restartu");
            exitItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            popup.add(exitItem);
            
            trayIcon.setPopupMenu(popup);
            
            tray.add(trayIcon);
        }
    }
    
    private void showWindowAbout(String title) {
        JFrame about = new JFrame(title);
        about.add(new About());
        about.pack();
        about.setVisible(true);
    }
    
    private void showWindowStatus(String title) {
        JFrame about = new JFrame(title);
        Status content = new Status();
        
        content.getlabelInterfacesCount().setText(String.valueOf(core.getNetworkManager().getLocalNetworkDevicesCount()));
        content.getLabelIpLocal().setText(core.getNetworkManager().getActiveDeviceIPasString());
        content.getLabelDevicesCount().setText(String.valueOf(core.getDeviceManager().devicesCount()));

        /**
         * GRAPH TODO: knihovna jung.jar
         */
        /*DirectedSparseGraph g = new DirectedSparseGraph();
         g.addVertex("Vertex1");
         g.addVertex("Vertex2");
         g.addVertex("Vertex3");
         g.addEdge("Edge1", "Vertex1", "Vertex2");
         g.addEdge("Edge2", "Vertex1", "Vertex3");
         g.addEdge("Edge3", "Vertex3", "Vertex1");
         VisualizationImageServer vs =
         new VisualizationImageServer(
         new CircleLayout(g), new Dimension(200, 200));
         // VisualizationImageServer vs = new VisualizationImageServer(new CircleLayout(g), new Dimension(200, 200));
         content.jPanelMap.add(vs);*/
        /**
         * GRAPH TEST
         */
        about.add(content);
        about.pack();
        about.setVisible(true);
        
    }
    
    public void setImageOK() {
        trayIcon.setImage(imgOk);
    }
    
    public void setImageWarning() {
        trayIcon.setImage(imgWarning);
    }
    
    public void setImageError() {
        trayIcon.setImage(imgError);
    }
    
}
