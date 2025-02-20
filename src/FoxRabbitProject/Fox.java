package FoxRabbitProject;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a fox.
 * Foxes age, move, eat rabbits, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Fox extends Animal implements Infectable
{
    // Characteristics shared by all foxes (class variables).
    
    // The age at which a fox can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a fox can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a fox breeding.
    private static double BREEDING_PROBABILITY = 0.09;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a fox can go before it has to eat again.
    private static final int RABBIT_FOOD_VALUE = 9;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The fox's age.
    private int age;
    // The fox's food level, which is increased by eating rabbits.
    private int foodLevel;

    //FoxRabbitProject.Fox's non-Infected color
    private static final Color defaultColor = Color.BLUE;
    //FoxRabbitProject.Fox's infected color
    private static final Color infectedColor = Color.MAGENTA;

    private static final Color immuneColor = Color.cyan;


    private static final int MAX_SICK_TIME = 13;

    private int countUntilCured;

    /**
     * Create a fox. A fox can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Fox(boolean randomAge, Field field, Location location, boolean immune)
    {
        super(field, location, immune);
        color = defaultColor;
        if(randomAge) {
            age = rand.nextInt((int) MAX_AGE);
            foodLevel = rand.nextInt(RABBIT_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = RABBIT_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the fox does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void act(List<Animal> newFoxes)
    {
        incrementAge();
        incrementHunger();

       // System.out.println("got here");

        if(isAlive()) {
            if(isInfected()) {
                spreadDisease();
                incrementCount();
            }
            giveBirth(newFoxes);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age. This could result in the fox's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this fox more hungry. This could result in the fox's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) {
                    if(this.isInfected()) {
                        rabbit.setDead();
                        foodLevel = RABBIT_FOOD_VALUE;
                    }


                    return where;
                }
            }
            /*Infect the FoxRabbitProject.Rabbit if the FoxRabbitProject.Fox is Sick
            else if(this.isInfected() && animal instanceof FoxRabbitProject.Rabbit) {
                FoxRabbitProject.Rabbit rabbit = (FoxRabbitProject.Rabbit) animal;
                if(rabbit.isAlive()) {

                    return where;
                }
                */

            //}
        }
        return null;
    }
    
    /**
     * Check whether or not this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born foxes.
     */
    private void giveBirth(List<Animal> newFoxes)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Fox young;
            if(this.isImmune()) {
                young = new Fox(false, field, loc, true);
            }
            else {
                young = new Fox(false, field, loc, false);
            }

            newFoxes.add(young);
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (maybe zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A fox can breed if it has reached the breeding age.
     */
    private boolean canBreed() {
        return age >= BREEDING_AGE;
    }

    /**
     * Portrays the stat change for Foxes once
     * they are infected.
     */
    @Override
    public void getInfected() {
        if(immune == false) {
            color = infectedColor;
            infected = true;
            //Kill the FoxRabbitProject.Fox if it is too young or too old
            if (age >= 130) setDead();
            //Affect the ability to reproduce while infected.
            if (infected) {
                age = age + 20;
            }
        }
    }

    public void incrementCount() {
        //decrease max sick time for more immune ones
        countUntilCured++;
        if(countUntilCured > MAX_SICK_TIME) {
            immune = true;
            infected = false;
            color = immuneColor;

        }
    }

    /**
     * The FoxRabbitProject.Fox itself will infect other animals while
     * it is infected.
     * @return
     */
    public Location spreadDisease() {

        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) {
                    rabbit.getInfected();
                    return where;
                }
            }
            else if(animal instanceof Fox) {
                Fox fox = (Fox) animal;
                if(fox.isAlive()) {
                    fox.getInfected();
                    return where;
                }
            }
        }
        return null;

    }
}
