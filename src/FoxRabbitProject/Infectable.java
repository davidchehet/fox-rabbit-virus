package FoxRabbitProject;

/**
 * An interface that specifies whether or not
 * an animal is able to be infected.
 *
 * @author davidchehet
 * @version 2.7.24
 */
public interface Infectable {

    void getInfected();

    Location spreadDisease();
}
