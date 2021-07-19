package Sandbox;

import Source.Benchmark;
import Source.Metric;
import Source.NetlabUT;

final class Sandbox extends NetlabUT
{
    public static void main(String[] args)
    {
        new Sandbox().run();
    }

    @Override
    protected void scenario() {

        {
            Metric<String> m = Benchmark.run(() -> greet("Ramadhan"));
            assumeEquals("Hello Ramadhan", m.returnValue);
        }
        
    }

    private String greet(String name) {
        return "Hello " + name; 
    }
}