import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A typing race simulation. Three typists race to complete a passage of text,
 * advancing character by character ÔÇö or sliding backwards when they mistype.
 *
 * Originally written by Ty Posaurus, who left this project to "focus on his
 * two-finger technique". He assured us the code was "basically done".
 * We have found evidence to the contrary.
 *
 * @author TyPosaurus
 * @version 0.7 (the other 0.3 is left as an exercise for the reader)
 */
public class TypingRace
{
    private int passageLength;   // Total characters in the passage to type
    private Typist[] typists; 
    private long startTime;

    // Accuracy thresholds for mistype and burnout events
    // (Ty tuned these values "by feel". They may need adjustment.)
    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int    SLIDE_BACK_AMOUNT   = 2;
    private static final int    BURNOUT_DURATION     = 3;

    /**
     * Constructor for objects of class TypingRace.
     * Sets up the race with a passage of the given length.
     * Initially there are no typists seated.
     *
     * @param passageLength the number of characters in the passage to type
     * @param totalSeats the total number of seats available
     */
    public TypingRace(int passageLength,int totalSeats)
    {
        this.passageLength = passageLength;
        typists = new Typist[totalSeats];
    }

    /**
     * Seats a typist at the given seat number (1, 2, or 3).
     *
     * @param theTypist  the typist to seat
     * @param seatNumber the seat to place them in (1ÔÇô3)
     */
    public void addTypist(Typist theTypist, int seatNumber)
    {
        if (seatNumber < 0 || seatNumber >= typists.length)
        {
            throw new IllegalArgumentException("Invalid seat number: " + (seatNumber+1));
        }
        typists[seatNumber] = theTypist;
    }

    /**
     * Starts the typing race.
     * All typists are reset to the beginning, then the simulation runs
     * turn by turn until one typist completes the full passage.
     *
     * Note from Ty: "I didn't bother printing the winner at the end,
     * you can probably figure that out yourself."
     */

    //resets race
    public void resetRace()
    {
        for (Typist typist : typists)
        {
            if (typist != null) typist.resetToStart();
        }
        startTime = System.nanoTime();
    }


    public Typist turn()
    {
        for (Typist typist : typists)
        {
            if (typist != null) advanceTypist(typist);
        }


        for (Typist typist : typists)
        {
            if (typist != null && raceFinishedBy(typist)) {
                return typist;
            }
        }
        return null;
    }

    /**
     * Simulates one turn for a typist.
     *
     * If the typist is burnt out, they recover one turn's worth and skip typing.
     * Otherwise:
     *   - They may type a character (advancing progress) based on their accuracy.
     *   - They may mistype (sliding back) ÔÇö the chance of a mistype should decrease
     *     for more accurate typists.
     *   - They may burn out ÔÇö more likely for very high-accuracy typists
     *     who are pushing themselves too hard.
     *
     * @param theTypist the typist to advance
     */
    private void advanceTypist(Typist theTypist)
    {
        if (theTypist.isBurntOut())
        {
            // Recovering from burnout ÔÇö skip this turn
            theTypist.recoverFromBurnout();
        }

        
        // Burnout check ÔÇö pushing too hard increases burnout risk
        // (probability scales with accuracy squared, capped at ~0.05)
        if (Math.random() < theTypist.getBurnOutChance())
        {
            theTypist.burnOut(BURNOUT_DURATION);
            theTypist.resetBurnOutChance();
        }
        // Mistype check ÔÇö the probability should reflect the typist's accuracy
        else if (Math.random() < (1-theTypist.getAccuracy()) * MISTYPE_BASE_CHANCE)
        {
            theTypist.slideBack(SLIDE_BACK_AMOUNT);
            theTypist.setMistyped(true);
        }
        // Attempt to type a character
        else
        {
            theTypist.typeCharacter();
            theTypist.incrementBurnOutChance();
        }
    }

    /**
     * Returns true if the given typist has completed the full passage.
     *
     * @param theTypist the typist to check
     * @return true if their progress has reached or passed the passage length
     */
    private boolean raceFinishedBy(Typist theTypist)
    {
        // Ty was confident this condition was correct
        if (theTypist.getProgress() >= passageLength)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Prints the current state of the race to the terminal.
     * Shows each typist's position along the passage, burnout state,
     * and a WPM estimate based on current progress.
     * @param startTime time when simulation started, used for wpm
     */


    /**
     * Prints a single typist's lane.
     *
     * Examples:
     *   |          Ôî¿           | TURBOFINGERS (Accuracy: 0.85)
     *   |    [zz]              | HUNT_N_PECK  (Accuracy: 0.40) BURNT OUT (2 turns)
     *
     * Note: Ty forgot to show when a typist has just mistyped. That would
     * be a nice improvement ÔÇö perhaps a [<] marker after their symbol.
     *
     * @param theTypist the typist whose lane to print
     * @param elapsedTime the elapsed time since the race started to calculate WPM
     */
    private void printSeat(Typist theTypist, long elapsedTime)
    {
        int spacesBefore = theTypist.getProgress();
        int spacesAfter  = passageLength - theTypist.getProgress();
        long elapsedSeconds = TimeUnit.NANOSECONDS.toSeconds(elapsedTime);


        //prevents division by zero when calculating WPM
        if(elapsedSeconds == 0){
            elapsedSeconds = 1; 
        }

        int wpm = (int) ((100 / elapsedSeconds)*60/5); //placeholder now that part 2 is in effect not sure if was meant to keep part1

        System.out.print('|');
        multiplePrint(' ', spacesBefore);

        // Always show the typist's symbol so they can be identified on screen.
        // Append [zz] when burnt out so the state is visible without hiding identity.
        // append [<] when just mistyped so the state is visible without hiding identity.
        System.out.print(theTypist.getSymbol());
        if (theTypist.isBurntOut())
        {
            System.out.print("[zz]");
            spacesAfter -= 4; // symbol + [zz] together take four characters
        }
        else if (theTypist.isMistyped())
        {
            System.out.print("[<]");
            spacesAfter -= 3; // symbol + [<] together take four characters
        }

        multiplePrint(' ', spacesAfter);
        System.out.print('|');
        System.out.print(' ');

        // Print name and accuracy
        if (theTypist.isBurntOut())
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")"
                + " BURNT OUT (" + theTypist.getBurnoutTurnsRemaining() + " turns)"
                + " WPM: " + wpm);
        }
        else if (theTypist.isMistyped())
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")"
                + " JUST MISTYPED"
                + " WPM: " + wpm);
            theTypist.setMistyped(false);
        }
        else
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")"
                + " WPM: " + wpm);
        }
    }

    /**
     * Prints a character a given number of times.
     *
     * @param aChar the character to print
     * @param times how many times to print it
     */
    private void multiplePrint(char aChar, int times)
    {
        int i = 0;
        while (i < times)
        {
            System.out.print(aChar);
            i = i + 1;
        }
    }

    /*
     * Prints the race configuration GUI.
     *Lets the user select passage length, seat count, and difficulty options.
     * When the user clicks submit, the GUI is cleared and it will move to the typist configuration GUI
     */
    public static void printRaceGUI()
    { 
        JLabel title = new JLabel("RACE CONFIGURATION");
        title.setFont(new Font("Arial",Font.BOLD,50));       

        JPanel options = new JPanel();
        options.setLayout(new GridLayout(3,2,20,20));
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Options");
        titledBorder.setTitleFont(new Font("Arial",Font.BOLD,20));
        options.setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(5,5,5,5)));
        
        JLabel passage_title = new JLabel("Choose passage length:");
        passage_title.setFont(new Font("Arial",Font.BOLD,20));
        passage_title.setSize(passage_title.getPreferredSize());

        options.add(passage_title);

        JLabel seat_count_title = new JLabel("Choose seat count:");
        seat_count_title.setFont(new Font("Arial",Font.BOLD,20));
        seat_count_title.setSize(seat_count_title.getPreferredSize());

        options.add(seat_count_title);

        String[] passages = {"short","medium","long","Custom"};
        JComboBox<String> passage_Selection = new JComboBox<String>(passages);
        passage_Selection.setFont(new Font("Arial",Font.BOLD,30));
        passage_Selection.setSize(passage_Selection.getPreferredSize());

        options.add(passage_Selection);
        
        Integer[] typists = {2,3,4,5,6};
        JComboBox<Integer> typistNumSelection = new JComboBox<Integer>(typists);
        typistNumSelection.setFont(new Font("Arial",Font.BOLD,20));
        typistNumSelection.setSize(typistNumSelection.getPreferredSize());

        options.add(typistNumSelection);

        int maxLength = 200;

        // Custom passage area is left invisible until user selects custome from passange options
        JLabel customPassageTitle = new JLabel("Write your passage. Characters left:"+maxLength);
        customPassageTitle.setFont(new Font("Arial",Font.BOLD,15));
        customPassageTitle.setSize(customPassageTitle.getPreferredSize());
        customPassageTitle.setVisible(false);

        JTextArea customPassageText = new JTextArea(3,20);
        customPassageText.setFont(new Font("Arial",Font.LAYOUT_LEFT_TO_RIGHT,20));
        customPassageText.setSize(customPassageText.getPreferredSize());
        customPassageText.setEditable(false);
        customPassageText.setVisible(false);
        customPassageText.setLineWrap(true);
        customPassageText.setWrapStyleWord(true);

        //keeps character count updated
        customPassageText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e){update();}
            @Override
            public void changedUpdate(DocumentEvent e){update();}
            @Override
            public void removeUpdate(DocumentEvent e){update();}

            public void update()
            {
                int charLeft = maxLength - customPassageText.getText().length();
                customPassageTitle.setText("Write your passage. Characters left:"+charLeft);
            }
        });

        //limits characters in custome passsage to maxLength
        ((AbstractDocument)customPassageText.getDocument()).setDocumentFilter(new DocumentSizeFilter(maxLength){});

        options.add(customPassageTitle);
        JScrollPane scroll = new JScrollPane(customPassageText);
        scroll.setBorder(null);
        options.add(scroll);

        JPanel difficulty = new JPanel();
        difficulty.setLayout(new GridLayout(3,1));
        difficulty.setVisible(true);
        titledBorder = BorderFactory.createTitledBorder("Difficulty options");
        titledBorder.setTitleFont(new Font("Arial",Font.BOLD,20));
        difficulty.setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(5,5,5,5)));

        JPanel autocorrect = new JPanel();
        titledBorder = BorderFactory.createTitledBorder("Autocorrect:");
        titledBorder.setTitleFont(new Font("Arial",Font.BOLD,20));
        autocorrect.setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(5,5,5,5)));
        autocorrect.setLayout(new FlowLayout());
        autocorrect.setVisible(true);

        JCheckBox autocorrectON = new JCheckBox();
        autocorrectON.setSize(autocorrectON.getPreferredSize());
        autocorrect.add(autocorrectON);

        JPanel caffeineMode = new JPanel();
        titledBorder = BorderFactory.createTitledBorder("Caffeine mode:");
        titledBorder.setTitleFont(new Font("Arial",Font.BOLD,20));
        caffeineMode.setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(5,5,5,5)));
        caffeineMode.setLayout(new FlowLayout());
        caffeineMode.setVisible(true);

        JCheckBox caffeineModeON = new JCheckBox();
        caffeineModeON.setSize(caffeineModeON.getPreferredSize());
        caffeineMode.add(caffeineModeON);

        JPanel nightShift = new JPanel();
        titledBorder = BorderFactory.createTitledBorder("Night shift:");
        titledBorder.setTitleFont(new Font("Arial",Font.BOLD,20));
        nightShift.setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(5,5,5,5)));
        nightShift.setLayout(new FlowLayout());
        nightShift.setVisible(true);

        JCheckBox nightShiftON = new JCheckBox();
        nightShiftON.setSize(nightShiftON.getPreferredSize());
        nightShift.add(nightShiftON);

        difficulty.add(autocorrect);
        difficulty.add(caffeineMode);
        difficulty.add(nightShift);

        Dimension d = difficulty.getPreferredSize();
        difficulty.setPreferredSize(new Dimension(Math.max(d.width, 320), d.height));

        JButton submitConfig = new JButton("SUBMIT");
        JPanel buttonPane = new JPanel();
        buttonPane.add(submitConfig);
        submitConfig.setPreferredSize(new Dimension(100,100));


        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLayout(new GridLayout(2,2));
        frame.add(title);
        frame.add(buttonPane);
        frame.add(options);
        frame.add(difficulty);
        frame.pack();

        //Reads all user information and stores it in variables from GUI
        submitConfig.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                int seatCount = (int)typistNumSelection.getSelectedItem();
                String passageLength = passage_Selection.getSelectedItem().toString();
                System.out.println(seatCount);
                String passage ="";
                if(passageLength.equals("short"))
                {
                    passage = "This is the short passage.";
                }    
                else if(passageLength.equals("medium"))
                {
                    passage = "This is the medium passage. Hello my name is whatever and I like whatever.";
                }
                else if(passageLength.equals("long"))
                {
                    passage = "This is the long passage. BLALALALALALALALALLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL";

                }
                else if(passageLength.equals("Custom"))
                {
                    passage = customPassageText.getText();
                    System.out.println(passage);
                }
                boolean autocorrectMOD = autocorrectON.isSelected();
                boolean caffeineMOD = caffeineModeON.isSelected();
                boolean nightShiftMOD = nightShiftON.isSelected();

                frame.getContentPane().removeAll();
                frame.revalidate();
                frame.repaint();

                int count = 0;
                TypingRace race = new TypingRace(passage.length(), seatCount);
                printTypistRaceGUI(frame,seatCount,autocorrectMOD,caffeineMOD,nightShiftMOD,count,race,passage);
            }
        });

        passage_Selection.addActionListener(e->{
            boolean customSelected = passage_Selection.getSelectedItem().equals("Custom");
            if(customSelected)
            {
                customPassageText.setVisible(true);
                customPassageTitle.setVisible(true);
                customPassageText.setEditable(true);
                options.revalidate();
                options.repaint();
                frame.revalidate();
                frame.pack();
            }
        });

    }

    /**
     * Prints the typist configuration GUI for each typist.
     * Lets the user select typing style, keyboard type, accessories, and a custom symbol for each typist.
     * @param frame frame carried on from previous GUI
     * @param seatCount number of seats
     * @param count keeps track of which typist we are configuring
     * @param race TypingRace object being used
     * @param autocorrectMOD 
     * @param caffeineMOD
     * @param nightShiftMOD
     * @param passage the passage being typed
    */
    public static void printTypistRaceGUI(JFrame frame,int seatCount,boolean autocorrectMOD,boolean caffeineMOD,boolean nightShiftMOD,
                                            int count,TypingRace race, String passage)
    {
        

        JLabel title = new JLabel("Typist "+(count+1)+" configuration");
        title.setFont(new Font("Arial",Font.BOLD,50));       

        JPanel typingStyle = new JPanel();
        typingStyle.setLayout(new GridLayout(3,2,20,20));
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Typist Options");
        titledBorder.setTitleFont(new Font("Arial",Font.BOLD,20));
        typingStyle.setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(5,5,5,5)));
        
        JLabel typingStyleTitle = new JLabel("Typing Style:");
        typingStyleTitle.setFont(new Font("Arial",Font.BOLD,20));
        typingStyleTitle.setSize(typingStyleTitle.getPreferredSize());

        typingStyle.add(typingStyleTitle);

        JLabel keyboardTypeTitle = new JLabel("Keyboard Type:");
        keyboardTypeTitle.setFont(new Font("Arial",Font.BOLD,20));
        keyboardTypeTitle.setSize(keyboardTypeTitle.getPreferredSize());

        typingStyle.add(keyboardTypeTitle);

        String[] typingStyles = {"Touch Typist","Hunt & Peck","Pho Thumbs","Voice-to-text"};
        JComboBox<String> typingStyleSelection = new JComboBox<String>(typingStyles);
        typingStyleSelection.setFont(new Font("Arial",Font.BOLD,30));
        typingStyleSelection.setSize(typingStyleSelection.getPreferredSize());

        typingStyle.add(typingStyleSelection);
        
        String[] keyboardTypes = {"Mechanical","Membrane","Touchscreen","Stenography"};
        JComboBox<String> keyboardTypeSelection = new JComboBox<String>(keyboardTypes);
        keyboardTypeSelection.setFont(new Font("Arial",Font.BOLD,30));
        keyboardTypeSelection.setSize(keyboardTypeSelection.getPreferredSize());

        typingStyle.add(keyboardTypeSelection);

        int maxLength = 1;
        
        JLabel customSymbol = new JLabel("Choose character symbol:");
        customSymbol.setFont(new Font("Arial",Font.BOLD,15));
        customSymbol.setSize(customSymbol.getPreferredSize());

        JTextArea customSymbolArea = new JTextArea(3,20);
        customSymbolArea.setFont(new Font("Arial",Font.LAYOUT_LEFT_TO_RIGHT,20));
        customSymbolArea.setSize(customSymbolArea.getPreferredSize());
        customSymbolArea.setEditable(true);

        ((AbstractDocument)customSymbolArea.getDocument()).setDocumentFilter(new DocumentSizeFilter(maxLength){});

        typingStyle.add(customSymbol);
        typingStyle.add(customSymbolArea);

        JPanel accessories = new JPanel();
        accessories.setLayout(new GridLayout(3,1));
        accessories.setVisible(true);
        titledBorder = BorderFactory.createTitledBorder("Accessories options");
        titledBorder.setTitleFont(new Font("Arial",Font.BOLD,20));
        accessories.setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(5,5,5,5)));

        JPanel wristSupport = new JPanel();
        titledBorder = BorderFactory.createTitledBorder("Wrist Support:");
        titledBorder.setTitleFont(new Font("Arial",Font.BOLD,20));
        wristSupport.setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(5,5,5,5)));
        wristSupport.setLayout(new FlowLayout());
        wristSupport.setVisible(true);

        JCheckBox wristSupportON = new JCheckBox();
        wristSupportON.setSize(wristSupportON.getPreferredSize());
        wristSupport.add(wristSupportON);

        JPanel energyDrink = new JPanel();
        titledBorder = BorderFactory.createTitledBorder("Energy Drink:");
        titledBorder.setTitleFont(new Font("Arial",Font.BOLD,20));
        energyDrink.setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(5,5,5,5)));
        energyDrink.setLayout(new FlowLayout());
        energyDrink.setVisible(true);

        JCheckBox energyDrinkON = new JCheckBox();
        energyDrinkON.setSize(energyDrinkON.getPreferredSize());
        energyDrink.add(energyDrinkON);

        JPanel noiseCancellingHeadphones = new JPanel();
        titledBorder = BorderFactory.createTitledBorder("Noise Cancelling Headphones:");
        titledBorder.setTitleFont(new Font("Arial",Font.BOLD,20));
        noiseCancellingHeadphones.setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(5,5,5,5)));
        noiseCancellingHeadphones.setLayout(new FlowLayout());
        noiseCancellingHeadphones.setVisible(true);

        JCheckBox noiseCancellingHeadphonesON = new JCheckBox();
        noiseCancellingHeadphonesON.setSize(noiseCancellingHeadphonesON.getPreferredSize());
        noiseCancellingHeadphones.add(noiseCancellingHeadphonesON);

        accessories.add(wristSupport);
        accessories.add(energyDrink);
        accessories.add(noiseCancellingHeadphones);

        Dimension d = accessories.getPreferredSize();
        accessories.setPreferredSize(new Dimension(Math.max(d.width, 320), d.height));

        JButton submitConfig = new JButton("SUBMIT");
        JPanel buttonPane = new JPanel();
        buttonPane.add(submitConfig);
        submitConfig.setPreferredSize(new Dimension(100,100));


        submitConfig.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                String name = "Typist "+(count+1);
                char symbol;
                try{
                    symbol = customSymbolArea.getText() != "" ? customSymbolArea.getText().charAt(0) : (char)('1'+count);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(frame, "Invalid symbol. Please enter a single character.");
                    return;
                }
                for(int i = 0; i < race.typists.length; i++)
                {
                    if(race.typists[i] != null && race.typists[i].getSymbol() == symbol)
                    {   
                        JOptionPane.showMessageDialog(frame, "Symbol already taken by another typist. Please choose a different symbol.");
                        return;
                    }
                }
                double accuracy = 0.5;
                String typingStyle = typingStyleSelection.getSelectedItem().toString();
                String keyboardType = keyboardTypeSelection.getSelectedItem().toString();
                boolean wristSupport = wristSupportON.isSelected();
                boolean energyDrink = energyDrinkON.isSelected();
                boolean noiseCancellingHeadphones = noiseCancellingHeadphonesON.isSelected();

                if(typingStyle.equals("Touch Typist"))
                {
                    accuracy += 0.3;
                }
                else if(typingStyle.equals("Hunt & Peck"))
                {
                    accuracy -= 0.1;
                }
                else if(typingStyle.equals("Pho Thumbs"))
                {
                    accuracy -= 0.2;
                }
                else if(typingStyle.equals("Voice-to-text"))
                {
                    accuracy += 0.1;
                }

                if(keyboardType.equals("Mechanical"))
                {
                    accuracy += 0.1;
                }
                else if(keyboardType.equals("Membrane"))
                {
                    accuracy -= 0.05;
                }
                else if(keyboardType.equals("Touchscreen"))
                {
                    accuracy -= 0.1;
                }
                else if(keyboardType.equals("Stenography"))
                {
                    accuracy += 0.2;
                }

                if(wristSupport)
                {
                    accuracy += 0.05;
                }
                if(energyDrink)
                {
                    accuracy += 0.05;
                }
                if(noiseCancellingHeadphones)
                {
                    accuracy += 0.05;
                }
                Typist new_typist = new Typist(symbol,name,accuracy);
                if (count + 1 < seatCount) {
                    race.addTypist(new_typist, count);
                    frame.getContentPane().removeAll();
                    printTypistRaceGUI(frame, seatCount, autocorrectMOD, caffeineMOD, nightShiftMOD, count+1, race, passage);
                }else{
                    race.addTypist(new_typist, count);
                    printSimGUI(frame, passage, autocorrectMOD, caffeineMOD, nightShiftMOD, race);
                }
            }
        });

        frame.setLayout(new GridLayout(2,2));
        frame.add(title);
        frame.add(buttonPane);
        frame.add(typingStyle);
        frame.add(accessories);
        frame.pack();
    }

    public static void printSimGUI(JFrame frame, String passage, boolean autocorrectMOD, boolean caffeineMOD, 
                            boolean nightShiftMOD, TypingRace race)
    {
        frame.getContentPane().removeAll();
        frame.getContentPane().setLayout(new BorderLayout());

        long startTime = System.nanoTime();

        JPanel all_typists = new JPanel();
        all_typists.setLayout(new GridLayout(race.typists.length, 1));

        JTextPane[] typistLanes = new JTextPane[race.typists.length];
        JScrollPane[] scrollPanes = new JScrollPane[race.typists.length];
        JPanel[] rows = new JPanel[race.typists.length];

        for (int i = 0; i < race.typists.length; i++)
        {
            DefaultCaret caret = new DefaultCaret(){
                @Override public boolean isVisible(){ return true; }
                @Override public boolean isSelectionVisible(){ return false; }
                @Override protected void positionCaret(MouseEvent e){ e.consume(); }
                @Override protected void moveCaret(MouseEvent e){ e.consume(); }
            };
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

            JTextPane new_lane = new JTextPane();
            new_lane.setEditable(false);
            new_lane.setCaret(caret);

            StyledDocument doc = new_lane.getStyledDocument();
            Style incomplete = doc.addStyle("incomplete", null);
            StyleConstants.setForeground(incomplete, Color.LIGHT_GRAY);
            try {
                doc.insertString(0, passage, incomplete);
            } catch (BadLocationException ex) {}

            typistLanes[i] = new_lane;
            JScrollPane scrollPane = new JScrollPane(typistLanes[i]);
            scrollPanes[i] = scrollPane;

            JLabel name = new JLabel(race.typists[i].getName() + " " + race.typists[i].getSymbol() + " (Accuracy: " + race.typists[i].getAccuracy() + ")");
            name.setFont(new Font("Arial", Font.BOLD, 20));

            JPanel row = new JPanel(new BorderLayout(0, 4));
            row.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            row.add(name, BorderLayout.NORTH);
            row.add(scrollPane, BorderLayout.CENTER);
            rows[i] = row; //stores reference to rows so it can be edited in an iterable fashion using the array

            all_typists.add(row);
        }

        frame.getContentPane().add(all_typists, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
        frame.getRootPane().paintImmediately(frame.getRootPane().getBounds());

        race.resetRace();

        Typist winner = null;
        while (winner == null)
        {
            winner = race.turn();
            for (int i = 0; i < race.typists.length; i++)
            {
                updateDisplay(typistLanes[i], passage, race.typists[i]);
                rows[i].paintImmediately(rows[i].getVisibleRect());
            }
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {}
        }

        JOptionPane.showMessageDialog(frame, "The winner is " + winner.getName() + "!");
        
        long elapsedTime = System.nanoTime() - startTime;
        long elapsedSeconds = TimeUnit.NANOSECONDS.toSeconds(elapsedTime); 


        for(Typist current_typist : race.typists){
            for(int i = 0;i < current_typist.getProgress(); i++){
                if(i == passage.length()-1){
                    current_typist.incrementCorrectWordsTyped();
                }
                else if(i != 0 && passage.charAt(i) == ' ' && passage.charAt(i-1) != ' ' ){
                    current_typist.incrementCorrectWordsTyped();
                }
            }
        }

        List<Typist> positions = new ArrayList<Typist>();
        for(Typist current_Typist : race.typists){
            positions.add(current_Typist);
        }
        positions.sort((a,b) -> {return -1 * ((Integer)a.getProgress()).compareTo((Integer)b.getProgress());});
        
        for(int i = 0; i< race.typists.length;i++){
            int position = positions.indexOf(race.typists[i]);
            race.typists[i].setRaceWPM((int)(race.typists[i].getCorrectWordsTyped()/elapsedSeconds)*60);
            race.typists[i].add_stats(position+1);
        }
        afterRaceGUI(frame, passage, autocorrectMOD, caffeineMOD, nightShiftMOD, race);
    }
    /**
     * 
     * @param pane
     * @param passage
     * @param typist
     * Updates the race display for each turn a typist takes
     */
    public static void updateDisplay(JTextPane pane, String passage, Typist typist)
    {
        StyledDocument doc = pane.getStyledDocument();
        int progress = typist.getProgress();

        Style gray = doc.addStyle("gray", null);
        StyleConstants.setForeground(gray, Color.LIGHT_GRAY);
        StyleConstants.setFontSize(gray, 20);

        Style green = doc.addStyle("green", null);
        StyleConstants.setForeground(green, new Color(34, 139, 34));
        StyleConstants.setFontSize(green, 20);

        doc.setCharacterAttributes(0, passage.length(), gray, true);

        int wordStart = 0;
        for (int i = 0; i <= passage.length(); i++)
        {
            if (i == passage.length() || passage.charAt(i) == ' ')
            {
                if (i <= progress)
                {
                    doc.setCharacterAttributes(wordStart, i - wordStart, green, true);
                }
                wordStart = i + 1;
            }

        }

        pane.setCaretPosition(Math.min(progress, passage.length()));
    }
    /**
     * 
     * @param frame
     * @param passage
     * @param autocorrectMOD
     * @param caffeineMOD
     * @param nightShiftMOD
     * @param race
     * Contains the logic for the post race GUI
     * displays typists hisory and statistics 
     * lets the user start another race
     */
    public static void afterRaceGUI(JFrame frame, String passage, boolean autocorrectMOD, boolean caffeineMOD, boolean nightShiftMOD
        , TypingRace race)
    {
        frame.getContentPane().removeAll();
        frame.getContentPane().setLayout(new BorderLayout());

        JPanel all_typists = new JPanel();
        all_typists.setLayout(new GridLayout(race.typists.length, 1));
        for(int i = 0; i< race.typists.length; i++)
        {
            List<HashMap<String,String>> history= race.typists[i].getStats();
            HashMap<String,String> lastrace = history.get(history.size()-1);
            double accuracyChange = (Integer.parseInt(lastrace.get("position")) == 1 ? 0.03 : 0 );
            JLabel typistStats = new JLabel("WPM: "+ race.typists[i].getRaceWPM() +". Best WPM: "+race.typists[i].getBestWPM() +". Accuracy percentage: "+ race.typists[i].getAccuracyPercentage()+ "%. Burnout Count: "+ race.typists[i].getBurnoutNum()+ ". Accuracy change: "+ accuracyChange);
            if(lastrace.get("position").equals("1")){
                race.typists[i].setAccuracy(race.typists[i].getAccuracy()+0.03);
            }
            JPanel statsPanel = new JPanel();
            statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
            statsPanel.add(typistStats);

            for(int j = 0; j< history.size();j++){
                HashMap<String,String> pastRaceStats = history.get(j); 
                JLabel pastRace = new JLabel("WPM: "+pastRaceStats.get("WPM") + ". Position: "+ pastRaceStats.get("position")+". Accuracy: "+ pastRaceStats.get("accuracy") + ". Burnouts: "+ pastRaceStats.get("burnouts"));
                pastRace.setBorder(BorderFactory.createTitledBorder("Race "+(j+1)+ "."));
                statsPanel.add(pastRace);
            }

            JLabel name = new JLabel(race.typists[i].getName() + " " + race.typists[i].getSymbol());
            name.setFont(new Font("Arial", Font.BOLD, 20));

            JPanel row = new JPanel(new BorderLayout(0, 4));
            row.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            row.add(name, BorderLayout.NORTH);
            row.add(statsPanel, BorderLayout.CENTER);


            all_typists.add(row);
        }

        JButton restartButton = new JButton("RESTART RACE");
        restartButton.setFont(new Font("Arial", Font.BOLD, 20));
        restartButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                printSimGUI(frame, passage, autocorrectMOD, caffeineMOD, nightShiftMOD, race);
            }
        });

        JPanel buttonPane = new JPanel();
        buttonPane.add(restartButton);

        frame.getContentPane().add(all_typists, BorderLayout.CENTER);
        frame.getContentPane().add(buttonPane,BorderLayout.EAST);
        frame.revalidate();
        frame.repaint();
    }
    public static void main(String[] args) {
        printRaceGUI();
    }
}
