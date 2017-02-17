package pl.agh.capo.simulation.visualization;

import com.google.gson.Gson;
import pl.agh.capo.utilities.communication.StateReceivedCallback;
import pl.agh.capo.utilities.maze.MazeMap;
import pl.agh.capo.utilities.maze.helper.MazeType;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.simulation.robot.RobotManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class MazeVisualizer extends JFrame implements StateReceivedCallback {

    private static final Dimension FRAME_SIZE = new Dimension(800, 660);
    private static final int SPLIT_DIVIDER_LOCATION = 600;

    private static final MazeVisualizer instance = new MazeVisualizer();

    private MazePanel mazePanel;
    private MazeMap mazeMap;

    private MazeVisualizer() {
        super("CAPO SIMULATION");
    }

    public static MazeVisualizer getInstance() {
        return instance;
    }

    public void open() throws FileNotFoundException {
        setJMenuBar(createMenuBar());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mazePanel = new MazePanel();
        setContentPane(createSplitPanel());
        setSize(FRAME_SIZE);
        setVisible(true);
        setResizable(false);

//        mazeMap = new Gson().fromJson(new FileReader(new File("C:\\Users\\Szymon\\git\\CapoVOController\\src\\main\\resources\\MazeRoboLabEmptyMapWithGate.roson")), MazeMap.class);

        
        //mazeMap = new Gson().fromJson(new FileReader(new File("D:\\Desktop\\Mapy\\ok\\OtwartaPrzestrze�5x5m.roson")), MazeMap.class);

        mazeMap = new Gson().fromJson(new FileReader(new File("D:\\Desktop\\Map.json")), MazeMap.class);

        mazePanel.setMaze(mazeMap);
		File robotConfig = new File("D:\\Desktop\\Config.csv");
		RobotManager robotManager = new RobotManager(robotConfig, mazeMap);
		//} catch (Exception e) {
//            CATCH THEM ALL!!!
      //}
    }
    
    
    public void open(String sMapPath) {
        setJMenuBar(createMenuBar());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mazePanel = new MazePanel();
        setContentPane(createSplitPanel());
        setSize(FRAME_SIZE);
        setVisible(true);
        setResizable(false);

        try {
            mazeMap =  new Gson().fromJson(new FileReader(new File(sMapPath)), MazeMap.class);
            mazePanel.setMaze(mazeMap);
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    

    private JMenuBar createMenuBar() {

        JMenu menu = new JMenu("Plik");
        menu.add(createMazeMapFileChooser());
        menu.add(createRobotConfigurationFile());
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);
        return menuBar;
    }

    private JMenuItem createRobotConfigurationFile() {
        JMenuItem menuItem = new JMenuItem("Wczytaj roboty...", KeyEvent.VK_T);
        menuItem.addActionListener(e -> {
                    if (mazeMap == null) {
                        JOptionPane.showMessageDialog(null, "Najpierw trzeba wybrać mapę pomieszczeń.");
                        return;
                    }
                    File chosenFile = chooseFile(new FileNameExtensionFilter("RobotConfiguration (*.csv)", "csv"));
                    if (chosenFile == null) {
                        return;
                    }
                    RobotManager robotManager = new RobotManager(chosenFile, mazeMap);
                }
        );
        return menuItem;
    }


    private JMenuItem createMazeMapFileChooser() {
        JMenuItem menuItem = new JMenuItem("Wczytaj mapę...", KeyEvent.VK_T);
        menuItem.addActionListener(e -> {
                    File chosenFile = chooseFile(new FileNameExtensionFilter("RoboMaze map (*.roson)", "roson"));
                    if (chosenFile == null) {
                        return;
                    }
                    Gson gson = new Gson();
                    try {
                        mazeMap = gson.fromJson(new FileReader(chosenFile), MazeMap.class);
                        mazePanel.setMaze(mazeMap);
                    } catch (FileNotFoundException e1) {
                        System.out.println("Could not read file: " + chosenFile.getName());
                    }
                }
        );
        return menuItem;
    }

    private File chooseFile(FileNameExtensionFilter filter) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "\\src\\main\\resources"));
        if (fileChooser.showOpenDialog(MazeVisualizer.this) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return fileChooser.getSelectedFile();
    }

    private JSplitPane createSplitPanel() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mazePanel, new Panel());
        splitPane.setDividerSize(5);
        splitPane.setDividerLocation(SPLIT_DIVIDER_LOCATION);
        splitPane.setEnabled(false);
        return splitPane;
    }

    @Override
    public void handle(State state) {
        mazePanel.addState(state);
    }
}
