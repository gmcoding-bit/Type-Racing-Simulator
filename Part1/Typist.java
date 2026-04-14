/**
 * Write a description of class Typist here.
 *
 * Starter code generously abandoned by Ty Posaurus, your predecessor,
 * who typed with two fingers and considered that "good enough".
 * He left a sticky note: "the slide-back thing is optional probably".
 * It is not optional. Good luck.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Typist
{
    // Fields of class Typist
    // Hint: you will need six fields. Think carefully about their types.
    // One of them tracks how far along the passage the typist has reached.
    // Another tracks whether the typist is currently burnt out.
    // A third tracks HOW MANY turns of burnout remain (not just whether they are burnt out).
    // The remaining three should be fairly obvious.

    private char typistSymbol;
    private String typistName;
    private double typistAccuracy;
    private int typistDistance;
    private boolean burntOut;
    private int burnOutRemaining;
    private boolean mistyped;
    private double burnOutChance; 



    // Constructor of class Typist
    /**
     * Constructor for objects of class Typist.
     * Creates a new typist with a given symbol, name, and accuracy rating.
     *
     * @param typistSymbol  a single Unicode character representing this typist (e.g. '①', '②', '③')
     * @param typistName    the name of the typist (e.g. "TURBOFINGERS")
     * @param typistAccuracy the typist's accuracy rating, between 0.0 and 1.0
     */
    public Typist(char typistSymbol, String typistName, double typistAccuracy)
    {
        this.typistSymbol = typistSymbol;
        this.typistName = typistName;
        this.typistAccuracy = typistAccuracy;
        this.typistDistance = 0;
        this.burntOut = false;
        this.burnOutRemaining = 0;
        this.mistyped = false;
        this.burnOutChance = 0.0;
    }


    // Methods of class Typist

    /**
     * Sets this typist into a burnout state for a given number of turns.
     * A burnt-out typist cannot type until their burnout has worn off.
     *
     * @param turns the number of turns the burnout will last
     */
    public void burnOut(int turns)
    {
        //can only burn out if not already burnt out
        if(!this.burntOut){
            this.burnOutRemaining = turns;
            this.burntOut = true;
        }
    }

    /**
     * Reduces the remaining burnout counter by one turn.
     * When the counter reaches zero, the typist recovers automatically.
     * Has no effect if the typist is not currently burnt out.
     */
    public void recoverFromBurnout()
    {
        if(this.burntOut){
            this.burnOutRemaining--;
        }
        if(this.burnOutRemaining <= 0){
            this.burntOut = false;
        }
    }

    /**
     * Returns the typist's accuracy rating.
     *
     * @return accuracy as a double between 0.0 and 1.0
     */
    public double getAccuracy()
    {
        return this.typistAccuracy;
    }

    /**
     * Returns the typist's current progress through the passage.
     * Progress is measured in characters typed correctly so far.
     * Note: this value can decrease if the typist mistypes.
     *
     * @return progress as a non-negative integer
     */
    public int getProgress()
    {
        return this.typistDistance; // placeholder - replace with correct implementation
    }

    /**
     * Returns the name of the typist.
     *
     * @return the typist's name as a String
     */
    public String getName()
    {
        return this.typistName; // placeholder - replace with correct implementation
    }

    /**
     * Returns the character symbol used to represent this typist.
     *
     * @return the typist's symbol as a char
     */
    public char getSymbol()
    {
        return this.typistSymbol; // placeholder - replace with correct implementation
    }

    /**
     * Returns the number of turns of burnout remaining.
     * Returns 0 if the typist is not currently burnt out.
     *
     * @return burnout turns remaining as a non-negative integer
     */
    public int getBurnoutTurnsRemaining()
    {
        if(this.burntOut){
            return this.burnOutRemaining; 
        }else{
            return 0;
        }
    }

    /**
     * Resets the typist to their initial state, ready for a new race.
     * Progress returns to zero, burnout is cleared entirely.
     */
    public void resetToStart()
    {
        this.burnOutRemaining = 0;
        this.typistDistance = 0;
        this.burntOut = false;   
    }

    /**
     * Returns true if this typist is currently burnt out, false otherwise.
     *
     * @return true if burnt out
     */
    public boolean isBurntOut()
    {
        return this.burntOut; 
    }

    /**
     * Advances the typist forward by one character along the passage.
     * Should only be called when the typist is not burnt out.
     */
    public void typeCharacter()
    {
        if(this.burntOut){
            return;
        }
        this.typistDistance++;
    }

    /**
     * Moves the typist backwards by a given number of characters (a mistype).
     * Progress cannot go below zero — the typist cannot slide off the start.
     *
     * @param amount the number of characters to slide back (must be positive)
     */
    public void slideBack(int amount)
    {
        if(this.typistDistance  - amount >= 0){
            this.typistDistance -= amount;
        }else{
            this.typistDistance = 0;
        }
    }

    /**
     * Sets the accuracy rating of the typist.
     * Values below 0.0 should be set to 0.0; values above 1.0 should be set to 1.0.
     *
     * @param newAccuracy the new accuracy rating
     */
    public void setAccuracy(double newAccuracy)
    {
        if(newAccuracy < 0.0){
            this.typistAccuracy = 0.0;
        }else if(newAccuracy > 1.0){
            this.typistAccuracy = 1.0;
        }else{          
            this.typistAccuracy = newAccuracy;
        }
    }

    /**
     * Sets the symbol used to represent this typist.
     *
     * @param newSymbol the new symbol character
     */
    public void setSymbol(char newSymbol)
    {
        this.typistSymbol = newSymbol;
    }

    /**
     * Sets the mistyped flag to the parameter value entered.
     * 
     * @param mistyped
     */
    public void setMistyped(boolean mistyped){
        this.mistyped = mistyped;
    }

    /**
     * returns the mistype status of the typist
     * 
     * @return true if the typist has mistyped, false otherwise
     */
    public boolean isMistyped(){
        return this.mistyped;
    }

    /**
     * Increments the burnout chance by 0.05 times the square of the typist's accuracy. 
     * Method is called when a typist increases their distance.
     */
    public void incrementBurnOutChance(){
        this.burnOutChance += 0.05*this.typistAccuracy*this.typistAccuracy;
    }
    
    /**
     * Resets the burnout chance to 0.0. 
     * Method is called when a typist recovers from burnout or mistypes.
     */
    public void resetBurnOutChance(){
        this.burnOutChance = 0.0;
    }

    /**
     * Returns the current burnout chance of the typist.
     * 
     * @return the burnout chance as a double
     */
    public double getBurnOutChance(){
        return this.burnOutChance;
    }

}
