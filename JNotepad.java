import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

public class JNotepad extends Frame {
	private static final int FILES_ONLY = 0;
	private JFrame frame;
	private JMenuBar jmb; 
	private JMenu jmuFile,jmuEdit,jmuFormat,jmuView,jmuHelp;
	private JMenuItem jmiNew, jmiOpen, jmiSave,jmiSaveAs,jmiPageSetup, jmiPrint,jmiExit;  //File
	private JMenuItem jmiUndo,jmiCut,jmiCopy,jmiPaste,jmiDelete, jmiFind , jmiFindNext, 
					  jmiReplace, jmiGoTo, jmiSelectionAll, jmiTmDt;						//Edit 
	private JMenuItem jmiWordWrap,jmiFont;												//Format
	private JMenuItem jmiStatusBar;														//View
	private JMenuItem jmiViewHelp, jmiJNotepad;											//Help
	private JMenuItem jmiClickCut,jmiClickCopy,jmiClickPaste;								//Copy.Paste,Cut
	private JPopupMenu jpu;
	private JFileChooser jfc, jfcSaveAs;
	private JLabel jlab;
	private String[] fonts;
	private JTextArea jta;
	private JTextField jtfFind,jtfReplaceField,preview;
	
	private JDialog jdialogFind; 
	private GregorianCalendar  gcalendar;
	private JList<?> jlistFont, jlistStyle, jlistSize; 
	private JScrollPane jscrlpFont,jscrlpSize,jscrlpStyle;
	private String months[]={ "Jan","Feb","Mar","Apr","May","Jun","Jul","Aug",
								"Sep","Oct","Nov","Dec"};
	private static final String[] fontStyle  = { "REGULAR", "ITALIC", "BOLD", "BOLD ITALIC"};
	private static final String[] fontSize = {"6", "8", "10", "12", "14", "16", "18", "20", 
						"22", "24", "36", "72"}; 
	private final Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
	private JLabel jlabFind,jlabReplace,currentlocation;
	private JCheckBox matchCase;
	private JButton jbtnFindNext,replace,replaceall;
	private String fontFamily,oldText,hour, month, date, minute, year, timeAnddate;
	private Pattern pat; private Matcher match;
	private int location,n; 
	private int style= Font.PLAIN;
	private int size = 12; 
	private Font ft;
	private JDialog jdFont; 
	
	//Constructor 
	JNotepad() {
		//Initialize Frame
		frame = new JFrame("Notepad");
		frame.setSize(1500,720);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new FlowLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//JFileChooser 
		jfc = new JFileChooser();
	
		//MenuBar
		jmb = new JMenuBar();
		
		//File Menu
		jmuFile = new JMenu("File");
		
		//MenuItem
		jmiNew = new JMenuItem("New", KeyEvent.VK_N);
		jmiNew.setAccelerator(KeyStroke.getKeyStroke((KeyEvent.VK_N),InputEvent.CTRL_MASK));
		jmiOpen = new JMenuItem("Open", KeyEvent.VK_O);
		jmiOpen.setAccelerator(KeyStroke.getKeyStroke((KeyEvent.VK_O), InputEvent.CTRL_MASK));
		jmiSave = new JMenuItem("Save" , KeyEvent.VK_S);
		jmiSave.setAccelerator(KeyStroke.getKeyStroke((KeyEvent.VK_S), InputEvent.CTRL_MASK));
		jmiSaveAs = new JMenuItem("Save As...");
		jmiPageSetup = new JMenuItem("Page Setup...");
		jmiPrint = new JMenuItem("Print...", KeyEvent.VK_P);
		jmiPrint.setAccelerator(KeyStroke.getKeyStroke((KeyEvent.VK_P), InputEvent.CTRL_MASK));
		jmiExit  = new JMenuItem("Exit"); 
	
		//Add JMenuItems into File
		jmuFile.add(jmiNew);
		jmuFile.add(jmiOpen);
		jmuFile.add(jmiSave);
		jmuFile.add(jmiSaveAs);
		JSeparator separator = new JSeparator();
		jmuFile.add(separator);
		jmuFile.add(jmiPageSetup);
		jmuFile.add(jmiPrint);
		JSeparator separator_1 = new JSeparator();
		jmuFile.add(separator_1);
		jmuFile.add(jmiExit);
		jmb.add(jmuFile);
		
		//EDIT Menu 
		jmuEdit = new JMenu("Edit");
		jmiUndo = new JMenuItem("Undo");
		jmiCut = new JMenuItem("Cut", KeyEvent.VK_X);
		jmiCut.setAccelerator(KeyStroke.getKeyStroke((KeyEvent.VK_X), InputEvent.CTRL_MASK));
		jmiCopy = new JMenuItem("Copy", KeyEvent.VK_C);
		jmiPrint.setAccelerator(KeyStroke.getKeyStroke((KeyEvent.VK_C), InputEvent.CTRL_MASK));
		jmiPaste = new JMenuItem("Paste", KeyEvent.VK_V);
		jmiPaste.setAccelerator(KeyStroke.getKeyStroke((KeyEvent.VK_V), InputEvent.CTRL_MASK));
		jmiDelete = new JMenuItem("Delete"); 
		jmiFind = new JMenuItem("Find...", KeyEvent.VK_F);
		jmiFind.setAccelerator(KeyStroke.getKeyStroke((KeyEvent.VK_F), InputEvent.CTRL_MASK));
		jmiFindNext = new JMenuItem("Find Next");
		jmiReplace = new JMenuItem("Replace...", KeyEvent.VK_H);
		jmiReplace.setAccelerator(KeyStroke.getKeyStroke((KeyEvent.VK_H), InputEvent.CTRL_MASK));
		jmiGoTo = new JMenuItem("Go To...", KeyEvent.VK_G);
		jmiGoTo.setAccelerator(KeyStroke.getKeyStroke((KeyEvent.VK_G), InputEvent.CTRL_MASK));
		jmiSelectionAll = new JMenuItem("Selection All", KeyEvent.VK_A);
		jmiSelectionAll.setAccelerator(KeyStroke.getKeyStroke((KeyEvent.VK_P), InputEvent.CTRL_MASK));
		jmiTmDt = new JMenuItem("Time/Date", KeyEvent.VK_P);
	
		jmuEdit.add(jmiUndo);
		JSeparator separator_2 = new JSeparator();
		jmuEdit.add(separator_2);
		jmuEdit.add(jmiCut);
		jmuEdit.add(jmiCopy);
		jmuEdit.add(jmiPaste);
		jmuEdit.add(jmiDelete);
		JSeparator separator_3 = new JSeparator();
		jmuEdit.add(separator_3);
		jmuEdit.add(jmiFind);
		jmuEdit.add(jmiFindNext);
		jmuEdit.add(jmiReplace);
		jmuEdit.add(jmiGoTo);
		JSeparator separator_4 = new JSeparator();
		jmuEdit.add(separator_4);
		jmuEdit.add(jmiSelectionAll);
		jmuEdit.add(jmiTmDt);
		jmb.add(jmuEdit);
		
		//Format
		jmuFormat = new JMenu("Format");
		jmiWordWrap = new JMenuItem("Word Wrap");
		jmiFont = new JMenuItem("Font");
		jmuFormat.add(jmiWordWrap);
		jmuFormat.add(jmiFont);
		jmb.add(jmuFormat);
	
		//View 
		jmuView = new JMenu("View");
		jmiStatusBar = new JMenuItem("Status Bar");
		jmuView.add(jmiStatusBar);
		jmb.add(jmuView);
		
		//Help Menu 
		jmuHelp = new JMenu("Help");
		jmiViewHelp = new JMenuItem("View Help");
		jmiJNotepad = new JMenuItem("About JNotepad");
		jmuHelp.add(jmiViewHelp);
		JSeparator separator_5 = new JSeparator();
		jmuHelp.add(separator_5);
		jmuHelp.add(jmiJNotepad);
		jmb.add(jmuHelp);
	
		//JTextArea
	   	jta = new JTextArea(100,205);
		jta.setFont(new Font ("Courier", Font.PLAIN, 12)); //Set Default 
			
		//JScrollPane 
		JScrollPane jsp = new JScrollPane(jta);
		
		//JMenuPopup for cut,copy and paste from mouse listener
		jpu = new JPopupMenu();
		jmiClickCut = new JMenuItem("Cut");
		jmiClickCopy = new JMenuItem("Copy");
		jmiClickPaste = new JMenuItem("Paste");
		jpu.add(jmiClickCut);
		jpu.add(jmiClickCopy);
		jpu.add(jmiClickPaste);
		
		//Mnemonics 
		jmiNew.setMnemonic('N');
		jmiPageSetup.setMnemonic('u');
		jmiExit.setMnemonic('x');
		jmiWordWrap.setMnemonic('W');
		jmiFont.setMnemonic('F');
		jmiStatusBar.setMnemonic('S');
		jmiViewHelp.setMnemonic('H');
		
		
		//Create Popup Menu for copy,cut,and paste into text area 
		jta.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				if (me.isPopupTrigger()) {
					jpu.show(me.getComponent(), me.getX(), me.getY());
				}
			}
			public void mouseReleased(MouseEvent me) {
				if(me.isPopupTrigger()) {
					jpu.show(me.getComponent(), me.getX(), me.getY());
				}
			}
		});
		
		//Objects 
		newFileListener newFile = new newFileListener();
		openFileListener openFile = new openFileListener();
		saveFileListener saveFile = new saveFileListener();
		saveAsFileListener saveAsFile = new saveAsFileListener();
		exitListener exit = new exitListener();
		cutListener cut =  new cutListener();
		copyListener copy = new copyListener();
		pasteListener paste = new pasteListener();
		deleteListener delete = new deleteListener();
		findAndFindnextListener findAndFindNext = new findAndFindnextListener();
		selectAllListener selectAll = new selectAllListener();
		TimeAndDateListener TimeAndDate = new TimeAndDateListener();
		wordWrapListener wordWrap = new wordWrapListener();
		JFontChooser font = new JFontChooser();
		aboutJNotepadListener aboutJNotepad = new aboutJNotepadListener();
		
		//ActionListeners
		jmiNew.addActionListener(newFile);
		jmiOpen.addActionListener(openFile);
		jmiSave.addActionListener(saveFile);
		jmiSaveAs.addActionListener(saveAsFile);
		jmiExit.addActionListener(exit);
		jmiCut.addActionListener(cut);
		jmiClickCut.addActionListener(cut);
		jmiCopy.addActionListener(copy);
		jmiClickCopy.addActionListener(copy);
		jmiPaste.addActionListener(paste);
		jmiClickPaste.addActionListener(paste);
		jmiDelete.addActionListener(delete);
		jmiFind.addActionListener(findAndFindNext);
		jmiFindNext.addActionListener(findAndFindNext);
		jmiSelectionAll.addActionListener(selectAll);
		jmiTmDt.addActionListener(TimeAndDate);
		jmiWordWrap.addActionListener(wordWrap);
		jmiFont.addActionListener(font);
		jmiJNotepad.addActionListener(aboutJNotepad );
		
		//Output
		frame.setJMenuBar(jmb);
		frame.getContentPane().add(jsp);
		frame.setVisible(true);
	
	}


class newFileListener implements ActionListener{
	public void actionPerformed(ActionEvent e) {
			jta.setText("");
	}
}
class JavaTextFileFilter extends FileFilter{

	@Override
	public boolean accept(File file) {
		if (file.getName().endsWith(".java") || file.getName().endsWith(".txt") )
			return true;
		if (file.isDirectory())
			return true;
		return false;
	}

	@Override
	public String getDescription() {
		return "Java Source Code Files" + "\n"+ "Text Source Code Files";
	}
	
}

//Open File- Done correctly 
class openFileListener implements ActionListener{
	public void actionPerformed(ActionEvent e) {
		try {
			jfc = new JFileChooser();
			jfc.setFileFilter(new JavaTextFileFilter());
			jfc.setFileSelectionMode(FILES_ONLY);
			int result = jfc.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				File openfile = jfc.getSelectedFile();
				FileReader filereader = new FileReader(openfile);
				BufferedReader br = new BufferedReader(filereader);
				jta.read(br,null);
				oldText = jta.getText();
				br.close();
			}
		}catch (Exception e2)
		{
		}
	}
	
}

class saveFileListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		String readText = (String) jta.getText();
		if (readText.length() != 0) {
			String message = "Do you want to save this file ?";	
			int result = JOptionPane.showConfirmDialog(null, message , null ,JOptionPane.YES_NO_OPTION);
			if (result == JFileChooser.APPROVE_OPTION ) {
				if (jfc.getSelectedFile().exists()) {
					try {
						BufferedWriter out = new BufferedWriter(new FileWriter(jfc.getSelectedFile().getPath()));
						String output = jta.getText();
						out.write(output);
						out.close();
					}catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			} 
		}
		
	}

}
		
//Save as Method 
class saveAsFileListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		jfcSaveAs = new JFileChooser();
		jfcSaveAs.setFileSelectionMode(FILES_ONLY);
		int result = jfcSaveAs.showSaveDialog(null);
		jfcSaveAs.setApproveButtonText("Save");
		if (result!= JFileChooser.APPROVE_OPTION)
			return;
		jfcSaveAs.setFileFilter(new JavaTextFileFilter());
		File file = jfcSaveAs.getSelectedFile();
		BufferedWriter outFile = null;
	    try {
	       outFile = new BufferedWriter(new FileWriter(file));
	       jta.write(outFile);
	    }catch (IOException ex) {
	        ex.printStackTrace();
	    } 
	}
  }

//Exit Method
class exitListener implements ActionListener{
	public void actionPerformed(ActionEvent ae) {
				System.exit(0);
	}
}

//Cut method
class cutListener implements ActionListener{
	public void actionPerformed(ActionEvent e) {
		String selection= jta.getSelectedText();
        if(selection==null){
            return;
        }
        StringSelection clipStr = new StringSelection(selection);
        clip.setContents(clipStr,clipStr);
        jta.replaceRange("", jta.getSelectionStart(), jta.getSelectionEnd());
		
	}
}

//Copy method
class copyListener implements ActionListener{
	public void actionPerformed(ActionEvent e) {
		jta.copy();
	}
}

//Paste method
class pasteListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		jta.paste();
    }
}
class deleteListener implements ActionListener{
	public void actionPerformed(ActionEvent e) {
		 String readText= jta.getSelectedText();
		 if(readText==null)
             return;
         int index = jta.getText().indexOf(readText);
         jta.replaceRange(" ",index,index+readText.length());
	
	}
}

class findAndFindnextListener implements ActionListener{
	public void actionPerformed(ActionEvent e) {
		
		jdialogFind = new JDialog(frame , "Find" , true);
        jdialogFind.setLocation((int)frame.getLocationOnScreen().getX()+200 ,
        		(int)frame.getLocationOnScreen().getY()+200);
        jdialogFind.setSize(400, 200);
        jdialogFind.setLayout(null);


        jtfFind = new JTextField(); 
        jlabFind = new JLabel("Find What:");
        jlabFind.setBounds(10, 10, 100, 20);
        jtfFind.setBounds(jlabFind.getX()+100, jlabFind.getY(), 150, 
        					jlabFind.getHeight());
        jdialogFind.add(jlabFind);
        jdialogFind.add(jtfFind);

        jtfReplaceField= new JTextField(); 
        jlabReplace = new JLabel("Replace With:");
        jlabReplace.setBounds(10, 60, 100, 20);
        jtfReplaceField.setBounds(jlabReplace.getX()+100, jlabReplace.getY(), 150,
        		jlabReplace.getHeight());
        jdialogFind.add(jlabReplace);
        jdialogFind.add(jtfReplaceField);

        matchCase = new JCheckBox("Match Case");
        matchCase.setBounds(270, 30, 150, 30);
        jdialogFind.add(matchCase);

        currentlocation = new JLabel();
        currentlocation.setBounds(10, 130, 200, 20);
        jdialogFind.add(currentlocation);

        jbtnFindNext = new JButton("Find Next");
        jbtnFindNext.setBounds(10,100,120,20);
        jbtnFindNext.addActionListener(new ActionListener(){
            int i=0;
            public void actionPerformed(ActionEvent e) {
                fontFamily = jta.getText();
                pat = Pattern.compile(jtfFind.getText());
                match= pat.matcher(fontFamily) ;
                if(match.find(i)){
                    jta.setSelectionStart(match.start());
                    jta.setSelectionEnd(i=match.end());
                } 
                else{
                    currentlocation.setText("No more words found");
                    currentlocation.repaint();
                }
            }
        });
        jdialogFind.add(jbtnFindNext);
        replace = new JButton("Replace");
        replace.setBounds(jbtnFindNext.getX()+jbtnFindNext.getWidth()+10,jbtnFindNext.getY(),
        					jbtnFindNext.getWidth(),jbtnFindNext.getHeight());
        replace.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String readText = jta.getText();
                Pattern pattern = Pattern.compile(jtfFind.getText());
                Matcher match = pattern.matcher(readText);
                if(match.find()){
                    System.out.println(match.start());
                    jta.setText(match.replaceFirst(jtfReplaceField.getText()));
                }
                else{
                    currentlocation.setText("No more words found");
                    currentlocation.repaint();
                }

            }

        });
        jdialogFind.add(replace);
        	replaceall = new JButton("Replace All");
        replaceall.setBounds(replace.getX() + replace.getWidth()+10,
        			jbtnFindNext.getY(),jbtnFindNext.getWidth(),jbtnFindNext.getHeight());
        replaceall.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e) {
	            String readText = jta.getText();
	            Pattern pattern;
	            if(matchCase.isSelected()){
	                 pattern = Pattern.compile(jtfFind.getText());
	            }else{
	                pattern = Pattern.compile(jtfFind.getText(), Pattern.CASE_INSENSITIVE);
	            }
	            Matcher match = pattern.matcher(readText);
	            int k=0;
	            if(match.find()){
	                int x=0;
	                while(match.find(x)){
	                		x=match.end();
	                	    k++;
	                }
	                jta.setText(match.replaceAll(jtfReplaceField.getText()));
	            }
	            if(k==0) {
	            		currentlocation.setText("No Words Found");
	            }else{
	            		currentlocation.setText(k +" Word replaced");
	            }
	            	currentlocation.repaint();
	        }
        });
        jdialogFind.add(replaceall);
        jdialogFind.setVisible(true);
        jdialogFind.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
     }
}

class selectAllListener implements ActionListener{
	public void actionPerformed(ActionEvent e) {
		String select = jta.getText();
        int length =select.length();
        jta.select(0,length);
	}
}


class TimeAndDateListener implements ActionListener{
	public void actionPerformed(ActionEvent e) {
		
		gcalendar = new GregorianCalendar();
        hour= String.valueOf(gcalendar.get(Calendar.HOUR));
        minute= String.valueOf(gcalendar.get(Calendar.MINUTE));
        date= String.valueOf(gcalendar.get(Calendar.DATE));
        month= months[gcalendar.get(Calendar.MONTH)];
        year= String.valueOf(gcalendar.get(Calendar.YEAR));
        timeAnddate= hour +":"+minute+ " "+ date+"/"+month+"/"+year;
        location= jta.getCaretPosition();
        jta.insert(timeAnddate,location);
		
	}
}

//Word Wrap Method
class wordWrapListener implements ActionListener{
	public void actionPerformed(ActionEvent e) {
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true); 
	}
}

//Cancel button is not working yet, Everything else is working
class JFontChooser implements ActionListener {
	public void actionPerformed(ActionEvent ar) {
	
		jdFont = new JDialog(frame, "font", true);
		jdFont.setLocation((int)frame.getLocationOnScreen().getX()+ 400, 
				(int)frame.getLocationOnScreen().getY()+400);
		jdFont.setSize(500, 500);
		jdFont.setLayout(null);
		
		jlab = new JLabel();
		preview = new JTextField("This is a preview.");
		preview.setHorizontalAlignment(SwingConstants.CENTER);
		preview.setBounds(67, 347, 363, 66);
		jdFont.add(preview);
	
		//Panel Font 	
		JLabel jlabFont = new JLabel("Font Family");
		jlabFont.setBounds(109, 28, 112, 16);
		jdFont.add(jlabFont);
		JPanel panelFont = new JPanel(); 
		panelFont.setBounds(16, 46, 292, 277);
		jdFont.add(panelFont);
		
		//Import Graphic Environment
		GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
		fonts = g.getAvailableFontFamilyNames();
		
	    jlistFont = new JList<Object> (fonts);
		jlistFont.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jscrlpFont = new JScrollPane(jlistFont);
		jscrlpFont.setPreferredSize(new Dimension(292,277));
		jdFont.add(jscrlpFont);
		
		preview.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
 				String text = a.getActionCommand();
 				jlab.setText(text);
 			}
 		});
		jlistFont.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					fontFamily = (String) jlistFont.getSelectedValue();
					ft = new Font(fontFamily, style, size);
					preview.setFont(ft);
				}
			}
		});
		jlab.setFont(new Font ("", n, size)); //Default 
		
		//Panel Style	
		JLabel jlabStyle = new JLabel("Style");
		jlabStyle.setBounds(366, 28, 64, 16);
		jdFont.add(jlabStyle);
		JPanel panelStyle = new JPanel();
		panelStyle.setBounds(320, 46, 140, 100);
		jdFont.add(panelStyle);
		jlistStyle = new JList<Object>(fontStyle);
		jlistStyle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jscrlpStyle = new JScrollPane(jlistStyle); 
		jscrlpStyle.setPreferredSize(new Dimension(140,100));
		
		jlistStyle.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					String s = (String) jlistStyle.getSelectedValue();
					if (s.equals("REGULAR"))
						style = Font.PLAIN;
					if (s.equals("BOLD"))
						style = Font.BOLD;
					if (s.equals("ITALIC"))
						style = Font.ITALIC;
					if (s.equals("BOLD ITALIC"))
						style = Font.BOLD + Font.ITALIC;
					ft = new Font(fontFamily, style, size);
					preview.setFont(ft);
				}
				
			}
		});
		
		// Panel Size 
		JLabel jlabSize = new JLabel("Size");
		jlabSize.setBounds(369, 163, 61, 16);
		jdFont.add(jlabSize);
		
		JPanel panelSize = new JPanel();
		panelSize.setBounds(320, 180, 138, 143);
		jdFont.add(panelSize);
		jlistSize = new JList<Object>(fontSize);
		jlistSize.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jscrlpSize = new JScrollPane(jlistSize); 
		jscrlpSize.setPreferredSize(new Dimension(138,143));
		jlistSize.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					String strSize = (String) jlistSize.getSelectedValue();
					size = Integer.parseInt(strSize); 
					ft = new Font(fontFamily, style, size);
					preview.setFont(ft);
				}
			}
		});
		
		JButton jbtnOk = new JButton("OK");
		jbtnOk.setBounds(267,425,117, 29);
		JButton jbtnCancel = new JButton("Cancel");
		jbtnCancel.setBounds(98,425,117,29);
		jdFont.add(jbtnOk);
		jdFont.add(jbtnCancel);
		jbtnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				jdFont.setVisible(false);
			}
		});
		jbtnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ft = new Font(fontFamily, style, size);
				jta.setFont(ft);
				jdFont.setVisible(true);
			}
		});
		panelFont.add(jscrlpFont);
		panelStyle.add(jscrlpStyle);
		panelSize.add(jscrlpSize);
		jdFont.setVisible(true);
		jdFont.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
		
}
class aboutJNotepadListener implements ActionListener{
	public void actionPerformed(ActionEvent e) {
		String message = "Microsoft Windows" + "\n" + "Version 1709(OS Build 16299.125)"
				+ "\n" + "Copyright Microsoft Corporation. All rights reserved." + 
				"\n" + "The windows 10 Home operating system and its user interface are"
				+ "\n" + "protected by trademark and other pending or existing intellectual property"
				+ "\n" + "rights in the United States and other countries/religions." + "\n\n\n" +
				"This production is licensed under the Microsoft Software License Terms to: ASUS 1";
				
				JOptionPane.showMessageDialog(frame, message, "About Notepad",
									JOptionPane.INFORMATION_MESSAGE);
	
	}
}
	//Main Class 
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new JNotepad();
			}
		});
	
	}

	
}
