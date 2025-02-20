This is a project I did that was part of an Object Oriented Design course at Rowan University which taught design patterns. We were given code by the authors of Objects First with Java: A Practical Introduction Using BlueJ, which including a Lotka–Volterra of Foxes and Rabbits.
Our task was to refactor the code to include a Virus, and our professor Dr. Ganesh Baliga gave us free reign to take this as far as we wanted it to go.
The simulation can be ran by running Main.java

Here is a general breakdown which explains what I refactored.

I created a brand new Virus.java class, by looking at the Fox.java class and refactoring it to have new properties.
Similarly I created a new interface called Infectable.java to allow animals to become infected. By using an interface I implemented **the strategy design pattern**. Virus.java also extends the Animal superclass to make future implementation easier.
This was done to maintain the open-closed principle of open for extension but closed for modification.

Foxes and Rabbits can become infected if they are adjacent to a virus. Becoming infected has several consequences:
  - Immune boolean is flagged as false
  - The animal changes color to the 'infected fox/rabbit' color state(more on that later)
  - If the animal is extremely old, they will die(to reflect real conditions)
  - If the animal survives it gains **immune status** and will produce **immune offspring**

I have already set the Virus' mutation probability to one that will let the simulation run for a good amount of time. If probability is over 0.022, the Virus quickly takes over the entire map.
    

Color Guide : 
- Virus -> Red
- Healthy Rabbit -> Orange
- Sick Rabbit -> Yellow
- **immune rabbit** -> Light Gray
- -------------------------------
- Healthy Fox -> Blue
- Sick Fox -> Magenta
- **immune fox** -> cyan
