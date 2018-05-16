package kavicsos_jatek.kavics;

import kavicsos_jatek.kavics.jatekablak;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class jatekablak_test {
	int mezok_test=0;
	
	@Before
	public void teszteles() {
	int col;
	int r;
	int[][] board = new int[4][4];
	
	for (r = 0 ; r < 4; r++) {
		for (col = 0; col < 4; col++) 
		{
			if (board[r][col] == 1)
				mezok_test++;
		}
	}
	}

	@Test
	public void test() 
	{
		Assert.assertTrue((mezok_test) == (jatekablak.mezok));
	}

}
