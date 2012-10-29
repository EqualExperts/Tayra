import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class Specs {
  @Test
  public void itRuns() {
    assertThat(true, is(true));
  }
}
