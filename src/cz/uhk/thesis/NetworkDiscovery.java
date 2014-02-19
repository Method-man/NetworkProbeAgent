
package cz.uhk.thesis;

import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.interfaces.Probe;
import cz.uhk.thesis.core.LogService;
import cz.uhk.thesis.view.About;
import cz.uhk.thesis.view.Status;
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

/**
 *
 * @author Filip Valenta
 */
public class NetworkDiscovery {
    
    private final Core core;

    public NetworkDiscovery() 
    {
        core = new Core();
        core.Init();
        
        this.initSystemTray();
    }
    
    private void initSystemTray()
    {
        if (!SystemTray.isSupported()) {
            LogService.Log2Console(this, "SystemTray is not supported");
        }
        else {
            final PopupMenu popup = new PopupMenu();
            
            BufferedImage img = null;
            try {
            img = ImageIO.read(new File("images/icon-16x16.png"));
            } catch (IOException e) {
                LogService.Log2Console(this, "SystemTray error reading image file");
            }
            
            final TrayIcon trayIcon = new TrayIcon(img, Language.APP_NAME);
            final SystemTray tray = SystemTray.getSystemTray();
            
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
            for(Probe p: core.getProbeLoader().GetProbes()) {
                CheckboxMenuItem cb = new CheckboxMenuItem(p.GetModuleName());
                cb.setState(true);
                cb.setEnabled(false); // TODO: moznost vypinat ?
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

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                LogService.Log2Console(this, "TrayIcon could not be added");
            }
        }
    }
    
    private void showWindowAbout(String title)
    {
        JFrame about = new JFrame(title);
        about.add(new About());
        about.pack();
        about.setVisible(true);
    }
    
    private void showWindowStatus(String title)
    {
        JFrame about = new JFrame(title);
        Status content = new Status();
        
        content.getlabelInterfacesCount().setText(String.valueOf(core.getNetworkManager().GetLocalNetworkDevicesCount()));
        content.getLabelIpLocal().setText(core.getNetworkManager().GetActiveDeviceIPasString());
        content.getLabelDevicesCount().setText(String.valueOf(core.GetDeviceManager().DevicesCount()));
        
        about.add(content);
        about.pack();
        about.setVisible(true);
       
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        new NetworkDiscovery();
        
    }

}
