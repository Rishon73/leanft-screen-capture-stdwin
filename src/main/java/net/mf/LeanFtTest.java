package net.mf;

import java.awt.image.RenderedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Date;

import com.hp.lft.report.Reporter;
import com.hp.lft.report.Status;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.*;

import com.hp.lft.sdk.*;
import com.hp.lft.sdk.stdwin.*;
import com.hp.lft.verifications.*;


import unittesting.*;

import javax.imageio.ImageIO;

@ExtendWith(UnitTestClassBase.class)
public class LeanFtTest {

    public LeanFtTest() {
        //Change this constructor to private if you supply your own public constructor
    }

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void testComboBox() throws GeneralLeanFtException, IOException, InterruptedException {

        // Launch the Notepad application.
        new ProcessBuilder("C:\\Windows\\System32\\notepad.exe").start();
        // Pause to ensure Notepad has fully opened on the computer.
        Thread.sleep(4 * 1000);

        // Locate the Notepad window and assign it to an Window object.
        Window notepadWindow = Desktop.describe(Window.class, new WindowDescription.Builder().windowClassRegExp("Notepad").windowTitleRegExp(" Notepad").build());
        getSnapshot("NotepadOpen", "Notepad is open");

        // Locate the Notepad menu and assign it to a Menu object.
        Menu notepadMenu = notepadWindow.describe(Menu.class, new MenuDescription(MenuType.MENU));

        // Build the path for the Font menu item, which is the second item in the Format menu in Notepad.
        String path = notepadMenu.buildMenuPath("Format", 2);
        // Use the path to retrieve the actual Font menu item object.
        MenuItem menuItem = notepadMenu.getItem(path);
        // Open the Font dialog using the font menu item.
        notepadMenu.select(menuItem);

        // Locate the Font dialog box and assign it to a Dialog object.
        Dialog notepadFontDialog = notepadWindow.describe(Dialog.class, new DialogDescription.Builder().windowTitleRegExp("Font").build());

        // Locate the Font combo box in the Font dialog box and assign it to a ComboBox object.
        ComboBox fontsComboBox = notepadFontDialog.describe(ComboBox.class, new ComboBoxDescription.Builder().attachedText("&Font:").nativeClass("ComboBox").build());
        // pick a random font from the list
        String font = getFont();

        // Select the font in the combo box.
        fontsComboBox.select(font);

        // Retrieve the selected combo box item
        String selectedFont = fontsComboBox.getSelectedItem();
        // Verify the selected combo box item is the selected font
        Verify.areEqual("Arial", selectedFont, "TestComboBox-Verify-Font", "Verify the selected combobox item is 'Arial'.");

        // Locate the Font combo box in the Font dialog box and assign it to a ComboBox object.
        ComboBox fontSizeComboBox = notepadFontDialog.describe(ComboBox.class, new ComboBoxDescription.Builder().attachedText("&Size:").nativeClass("ComboBox").build());
        fontSizeComboBox.select(getFontSize());
        getSnapshot("SelectedFont", "Font type and size is selected");
        Thread.sleep(1000);

        // Locate the OK button in the dialog box and assign it to a Button object.
        Button oKButton = notepadFontDialog.describe(Button.class, new ButtonDescription.Builder().nativeClass("Button").text("OK").build());
        // Click "OK" in the dialog box.
        oKButton.click();

        // The texxxt to write to notepad
        String message = "private void getSnapshot(String stepName, String stepDesc){\n" +
                "        String fileName = null;\n" +
                "        String dateName = new SimpleDateFormat(\"_yyyyMMddhhmmss\").format(new Date());\n" +
                "        RenderedImage renderedImage = null;\n" +
                "        try{...}catch (GeneralGridException e){\n" +
                "            e.printStackTrace();\n" +
                "        }\n" +
                "    }";
        Editor editor = notepadWindow.describe(Editor.class, new EditorDescription.Builder().nativeClass("Edit").windowClassRegExp("Edit").build());
        editor.sendKeys(message);
        getSnapshot("TextInNotepad", "Text was typed to notepad");
        Thread.sleep(1000);

        notepadWindow.close();
/*
        // Build the path for the Exit menu item, which is the seventh item in the File menu in Notepad.
        path = notepadMenu.buildMenuPath("File", 7);
        menuItem = notepadMenu.getItem(path);
        // Exit and close Notepad.
        notepadMenu.select(menuItem);
*/
        getSnapshot("BeforeDoNotSavine", "Right before clicking the 'Don't Save'");

        Thread.sleep(1000);
        // Locate and click the Don't Save button
        notepadWindow.describe(Dialog.class, new DialogDescription.Builder()
                .childWindow(false)
                .ownedWindow(true)
                .nativeClass("#32770")
                .text("Notepad").build())
                .describe(Button.class, new ButtonDescription.Builder()
                        .nativeClass("Button")
                        .text("Do&n't Save").build()).click();

        getSnapshot("AfterNotepadClosed", "This event happened after Notepad was closed");
    }

    // To make it interesting randomize font face
    private String getFont(){
        String[] fonts = {"Arial", "Courier", "Arial", "Georgia"};
        int n = new Random().nextInt(4);
        System.out.println(String.format("%s is tttthe selected font", fonts[n]));
        return fonts[n];
    }

    // To make it interesting randomize font size
    private String getFontSize(){
        String[] sizes = {"14", "16", "18", "20"};
        int n = new Random().nextInt(4);
        System.out.println(String.format("%s is the seleted font size", sizes[n]));
        return sizes[n];
    }

    private void getSnapshot(String stepName, String stepDesc){
        String fileName = null;
        String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        RenderedImage renderedImage = null;
        try{
            Window window = Desktop.describe(Window.class, new WindowDescription.Builder().active(true).build());
            renderedImage = window.getSnapshot();

            // Put the snapshot in the LeanFT report
            Reporter.reportEvent(stepName, stepDesc, Status.Passed, renderedImage);

            // Save the image to a file
            fileName = String.format("%s\\%s\\Resources\\User\\%s_%s.png",Reporter.getReportConfiguration().getTargetDirectory() ,
                    Reporter.getReportConfiguration().getReportFolder(), stepName, dateName);
            System.out.println("Saving '" + fileName + "'");
            ImageIO.write(renderedImage, "png", new File(fileName));

        }catch (GeneralGridException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}