package FoxRabbitProject;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Rabbit extends Animal implements Infectable
{
    // Characteristics shared by all rabbits (class variables).

    // The age at which a rabbit can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a rabbit can live.
    private static double MAX_AGE = 40.0;
    // The likelihood of a rabbit breeding.
    private static double BREEDING_PROBABILITY = 0.13;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    //Rabbits Healthy Color
    private static final Color defaultRabbitColor = Color.ORANGE;

    //Rabbits Infected Color
    private static final Color infectedRabbitColor = Color.YELLOW;

    private static final int MAX_SICK_TIME = 13;

    private int countUntilCured;

    private static final Color immuneColor = Color.lightGray;





    // Individual characteristics (instance fields).
    
    // The rabbit's age.
    private int age;

    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a newborn) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Rabbit(boolean randomAge, Field field, Location location, boolean immune)
    {
        super(field, location, immune);
        color = defaultRabbitColor;
        age = 0;
        if(randomAge) {
            age = rand.nextInt((int) MAX_AGE);
        }
    }
    
    /**
     * This is what the rabbit does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newRabbits A list to return newly born rabbits.
     */
    public void act(List<Animal> newRabbits)
    {
        incrementAge();

        if(isAlive()) {
            if(isInfected()) {
                spreadDisease();
                incrementCount();
            }
            giveBirth(newRabbits);            
            // Try to move into a free location.
            Location newLocation = getField().freeAdjacentLocation(getLocation());
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
     * Increase the age.
     * This could result in the rabbit's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this rabbit is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newRabbits A list to return newly born rabbits.
     */
    private void giveBirth(List<Animal> newRabbits)
    {
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Rabbit young;
            if(this.isImmune()) {
                young = new Rabbit(false, field, loc, true);
            }
            else {
                young = new Rabbit(false, field, loc, false);
            }
            newRabbits.add(young);
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
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
     * A rabbit can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * Changes statistics for an infected FoxRabbitProject.Rabbit.
     */
    @Override
    public void getInfected() {
        color = infectedRabbitColor;
        infected = true;
        //Kill the rabbit if it is too young or very old.
        if(age >= 35) setDead();
        //Affect infected animals ability to breed while infected.
        if (infected) {
            age = age + 20;
        }

    }

    /**
     * Time to become immune to the virus.
     */
    public void incrementCount() {
        //decrease max sick time for more immune ones
        countUntilCured++;
        if(countUntilCured > MAX_SICK_TIME) {
            immune = true;
            infected = false;
            color = immuneColor;

        }
    }


    @Override
    public Location spreadDisease() {

        Field field1 = getField();
       // System.out.println(field1);
        List<Location> adjacent = field1.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field1.getObjectAt(where);
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
