package Sandbox;

import Source.Metric;
import Source.TestCase;
import UnderTest.ALU;

final class Sandbox extends TestCase
{
    public static void main(String[] args)
    {
        new Sandbox().run();        
    }

    @Override
    protected void scenario() {
        testCompare(
            ()->
            {
                String h = "Hello World!";
            },
            ()->
            {
                String v = "Hello People from Arround the World! It is pleasure to meet you!";
            }
        ); 
    }
}