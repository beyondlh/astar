package com.lh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.Vector;

public class AStar extends JPanel implements ItemListener, ActionListener {
    boolean isDesign = false;
    CheckboxGroup group;
    Checkbox blocks, start, finish;
    Choice level = new Choice();
    Choice method = new Choice();
    Choice preset = new Choice();
    Choice user = new Choice();
    Button go, clear, save, load, userSave;

    LHMap LHMap = new LHMap();
    PathFinder finder = new HuristicAStar();//new OneTailAStar();

    public void init() {
        //{{INIT_CONTROLS
        setLayout(new BorderLayout(0, 0));
        setSize(612, 482);
        //}}
        Panel m = new Panel();
        method.add("Classic A*");
        method.add("Old");
        method.add("Fudge");
        user.add("Author");
        user.add("Users");
        if (!isDesign) {
            //we are an applet
            // userSave = new Button("Save");
            // m.add(new Label("LHMap Set:"));
            // m.add(user);
            m.add(new Label("Load Map:"));
            m.add(preset);
            //  m.add(userSave);
            findSavedMaps("Saves");

        }
        m.add(new Label("Method:"));
        m.add(method);
        add(m, "North");
        add(LHMap, "Center");
        Panel p = new Panel();
        p.setLayout(new GridLayout(4, 1));
        Panel b = new Panel();
        b.setLayout(new GridLayout(2, 1));
        level.add("Impossible");
        level.add("Very Tough (" + GridCell.VERY_TOUGH + ")");
        level.add("Tough (" + GridCell.TOUGH + ")");
        level.add("Normal (" + GridCell.NORMAL + ")");
        level.add("Easy (" + GridCell.EASY + ")");

        level.addItemListener(this);
        preset.addItemListener(this);
        method.addItemListener(this);

        group = new CheckboxGroup();
        blocks = new Checkbox("设置障碍点", group, true);
        start = new Checkbox("设置起点", group, false);
        finish = new Checkbox("设置止点", group, false);
        blocks.addItemListener(this);
        start.addItemListener(this);
        finish.addItemListener(this);
        b.add(blocks);
        b.add(level);

        p.add(b);
        p.add(start);
        p.add(finish);
        add(p, "East");
        Panel g = new Panel();
        if (!isDesign) {
            g.setLayout(new GridLayout(3, 1, 2, 10));
        } else {
            g.setLayout(new GridLayout(2, 2, 2, 30));
        }
        go = new Button("  开始  ");
        save = new Button("保存地图");
        load = new Button("加载地图");
        clear = new Button("清除");
        g.add(go);
        g.add(clear);

        if (isDesign) {
            g.add(save);
            g.add(load);
        }
        p.add(g);
        go.addActionListener(this);
        clear.addActionListener(this);
        save.addActionListener(this);
        load.addActionListener(this);
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == level) {
            blocks.setState(true);
            GridCell.setEditMode(GridCell.SET_BLOCKS);
            switch (level.getSelectedIndex()) {
                case 0:
                    GridCell.setNewBlockStrength(GridCell.BLOCK);
                    return;
                case 1:
                    GridCell.setNewBlockStrength(GridCell.VERY_TOUGH);
                    return;
                case 2:
                    GridCell.setNewBlockStrength(GridCell.TOUGH);
                    return;
                case 3:
                    GridCell.setNewBlockStrength(GridCell.NORMAL);
                    return;
                case 4:
                    GridCell.setNewBlockStrength(GridCell.EASY);
                    return;
                default:
                    GridCell.setNewBlockStrength(GridCell.NORMAL);
                    return;
            }
        }
        if (e.getSource() == method) {
            switch (method.getSelectedIndex()) {
                case 0:
                    finder = new HuristicAStar();
                    System.out.println("Switched to Huristic A*");
                    break;
                case 1:
                    finder = new OneTailAStar();
                    System.out.println("Switched to OneTailAStar");
                    break;
                case 2:
                    finder = new Fudge();
                    System.out.println("Switched to version of Amit Patel's huristic");
                    break;
            }
            return;
        }


        if (e.getSource() == preset) {
            //load in a file as an applet
            System.out.println("request to load " + preset.getSelectedItem());
            loadPreDef();
            return;
        }

        Checkbox box = group.getSelectedCheckbox();
        if (box == blocks) {
            GridCell.setEditMode(GridCell.SET_BLOCKS);
            return;
        }
        if (box == start) {
            GridCell.setEditMode(GridCell.SET_START);
            return;
        }
        if (box == finish) {
            GridCell.setEditMode(GridCell.SET_FINISH);
            return;
        }

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == go) {
            finder.findPath(LHMap);
        }
        if (e.getSource() == clear) {
            GridCell.clearAll();
            LHMap.repaint();
        }
        if (e.getSource() == save) {
            if (isDesign) {
                try {
                    FileDialog fd = new FileDialog(new Frame(), "Save a grid", FileDialog.SAVE);
                    fd.setFile("*.grd");
                    fd.setDirectory("Saves");
                    fd.setFilenameFilter(new FilenameFilter() {
                        public boolean accept(File f, String n) {
                            System.out.println("accept requested");
                            return n.endsWith("grd");
                        }
                    });
                    fd.setVisible(true);
                    File file = new File(fd.getDirectory(), fd.getFile());
                    FileOutputStream fos = new FileOutputStream(file);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(LHMap);
                    oos.flush();
                    oos.close();
                    System.out.println("Grid Saved");
                } catch (IOException ex) {
                    System.err.println("Save failed " + ex);
                }
            } else {
                savePreDef();
            }


        }
        if (e.getSource() == load) {
            try {
                FileDialog fd = new FileDialog(new Frame(), "Select Map to Load", FileDialog.LOAD);
                fd.setFile("*.grd");
                fd.setDirectory("Saves");
                fd.setFilenameFilter(new FilenameFilter() {
                    public boolean accept(File f, String n) {
                        System.out.println("accept requested");
                        return n.endsWith("grd");
                    }
                });
                fd.setVisible(true);
                File file = new File(fd.getDirectory(), fd.getFile());
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                remove(LHMap);
                LHMap = (LHMap) ois.readObject();
                add(LHMap, "Center");
                ois.close();
                System.out.println("Grid Loaded");
            } catch (Throwable ex) {
                System.err.println("Load failed " + ex);
                add(LHMap, "Center");
            }

        }
    }

    private void loadPreDef() {
        LHMap temp = LHMap;
        try {
            URL load = this.getClass().getResource("");
            ObjectInputStream ois = new ObjectInputStream(load.openStream());
            remove(LHMap);
            LHMap = (LHMap) ois.readObject();
            add(LHMap, "Center");
            LHMap.invalidate();
            ois.close();
            System.out.println("Grid Loaded");
            invalidate();
        } catch (Throwable ex) {
            System.err.println("Load failed " + ex);
            add(temp, "Center");
        }
    }

    private void savePreDef() {
        LHMap temp = LHMap;
        try {
            URL save = this.getClass().getResource("");//gridName.getText());

            ObjectOutputStream oos = new ObjectOutputStream(save.openConnection().getOutputStream());
            oos.writeObject(LHMap);
            oos.flush();
            oos.close();
            System.out.println("Grid Saved");
        } catch (Throwable ex) {
            System.err.println("Load failed " + ex);
            add(temp, "Center");
        }
    }


    public Dimension getPreferredSize() {
        return new Dimension(520, 420);
    }

    public void findSavedMaps(String folder) {
        try {
            URL test = this.getClass().getResource("");
            preset.removeAll();
            System.out.println(test);
            //  System.out.println(test.getFile());
            InputStream buff = (InputStream) test.openConnection().getInputStream();
            StreamTokenizer st = new StreamTokenizer(new InputStreamReader(buff));
            int type = st.nextToken();
            int trys = 0;
            Vector files = new Vector();
            String last = "";
            while (type != -1 && trys < 1000) {
                //System.out.println(st.sval);
                if (st.sval != null && st.sval.endsWith(".grd") && !st.sval.equals(last)) {
                    files.addElement(st.sval);
                    last = st.sval;
                }
                type = st.nextToken();
                trys++;
            }
            for (int i = 0; i < files.size(); i++) {
                preset.add(files.elementAt(i).toString());
            }
            if (files.size() > 0) {
                loadPreDef();
            }

        } catch (Throwable e) {
            System.out.println(e);
        }
    }


    public static void main(String args[]) {
        AStar app = new AStar();
        app.isDesign = true;
        Frame f = new Frame();
        f.setSize(612, 482);
        f.setResizable(false);
        f.setLayout(new BorderLayout());
        //f.add(new Button("Save"));
        f.add(app, "Center");
        f.setTitle("A*算法学习测试");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        app.init();

        f.setVisible(true);
    }
}