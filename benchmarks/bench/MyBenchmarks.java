package bench;

import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
/**
 * Caliper tutorial. To run the example benchmarks in this file:
 * {@code CLASSPATH=... [caliper_home]/caliper tutorial.Tutorial.Benchmark1}
 */
public class MyBenchmarks {


    static class EntityValueException extends Exception {
      private final byte[] family;
      private final byte[] qualifier;
      private final String tag;

      //default tag
      public static final String NO_VALUE = "NO_VALUE";

      /**
       * Create a new value exception providing the family:qualifier column
       * and the tag associated for this exception
       * @param family
       * @param qualifier
       * @param tag
       */
      public EntityValueException(byte[] family, byte[] qualifier, String tag) {
        this.family = family;
        this.qualifier = qualifier;
        this.tag = tag;
      }

      /**
       * Create a new value exception providing the family:qualifier column
       * Default {@link #NO_VALUE} tag is assigned to this exception
       * @param family
       * @param qualifier
       */
      public EntityValueException(byte[] family, byte[] qualifier) {
        this(family, qualifier, NO_VALUE);
      }

      @Override
      public Throwable fillInStackTrace() {
        // we are making this exception stackless (i.e faster)
        return null;
      }

      @Override
      public String getMessage() {
        return (hasTag() ? tag : "") + ": Unable to retrieve row at " + family + ":" + qualifier;
      }

      public boolean hasTag() {
        return this.tag != null;
      }

      /**
       * @return the tag associated with this question
       */
      public String getTag() {
        return this.tag;
      }
    }

  public static class ReturnBool extends SimpleBenchmark {
    @Param int size; // set automatically by framework

    protected int[] array; // set by us, in setUp()

    @Override protected void setUp() {
      // @Param values are guaranteed to have been injected by now
      array = new int[size];
      for (int i=0; i<size; i++){
        if (i%4==0) array[i]=0;
        else array[i]=i;
      }

      //shuffle array
      Collections.shuffle(Arrays.asList(array), new Random(0));
    }

    private boolean checkBool(int number) {
          if (number == 0) {
                return true;
          }
          return false;
    }

    public int timeReturnBool(int reps) {
      int dummy = 0;
      for (int i = 0; i < reps; i++) {
        for (int doNotIgnoreMe : array) {
             if (checkBool(doNotIgnoreMe)) { dummy+=1; }
        }
      }
      System.out.println("reps"+reps+"dummy: "+dummy);
      return dummy;
    }
  }

  public static class ThrowException extends SimpleBenchmark {
    protected EntityValueException e;
    @Param int size; // set automatically by framework

    protected int[] array; // set by us, in setUp()

    @Override protected void setUp() {
      // @Param values are guaranteed to have been injected by now
      array = new int[size];
      for (int i=0; i<size; i++){
        if (i%4==0) array[i]=0;
        else array[i]=i;
      }

      //shuffle array
      Collections.shuffle(Arrays.asList(array), new Random(0));

      e = new EntityValueException(new byte[10], new byte[10]);
    }

    private void checkException(int number) throws EntityValueException {
          if (number == 0) {
             throw e;
          }
    }
    public int timeThrowException(int reps) {
      int dummy = 0;
      for (int i = 0; i < reps; i++) {
        for (int doNotIgnoreMe : array) {
            try {checkException(doNotIgnoreMe); } catch (Exception e1) { dummy+=1; }
        }
      }
      System.out.println("reps"+reps+"dummy: "+dummy);
      return dummy;
    }

  }
}
