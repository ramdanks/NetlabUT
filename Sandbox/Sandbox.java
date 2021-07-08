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
        testCompare(ALU.add(1, 4), ALU.add(5, 1));
        ALU::add
    }
}