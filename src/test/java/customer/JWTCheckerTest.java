package customer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class JWTCheckerTest {
	private JWTChecker checker;
	
	@Before
	public void init() {
		checker = new JWTChecker("l6kHLmBzbzdBi8eznmg-nGP3pZihFpLgie0K-SKEUARXzyG5yJ7nnZoFPVL_MvoJckcoRslQZlxsNZpVruKqrP0s-m6YDaKw4Ody8lZbPmQ7wU0Mc1561J01VH55s4apg_TvnOJ6xn8sEfid3gPbvXntIb0hokVtopG5Zaam2wfyMuBlzqhQ42TtL51yZOym5NLad4_WtZUG9kM204K7OgLHFfUABpWD4ocBF7uQrebcO4cwqlVI6N_A_252TdEkfOyh_vG4yxO0h88LxaGSXj0iKFpO4q6md7bFMjzdehtPKiqWktKg02QyAjq-PRcrSiWBZfh2w6JIkU7g8kS5YQ");
	}
	
	@Test
	public void canary() {
		assertThat(true, is(equalTo(true)));
	}
	
	@Test
	public void checkJWT_null() {
		assertThat(checker.checkJWTHeader(null), is(equalTo("Invalid JWT token")));
	}

}
