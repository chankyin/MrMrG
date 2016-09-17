package savedobject;

import java.io.*;

import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.junit.Assert;
import org.junit.Test;

import me.noip.chankyin.mrmrg.utils.io.SavedObject;
import me.noip.chankyin.mrmrg.utils.io.SavedObjectInputStream;
import me.noip.chankyin.mrmrg.utils.io.SavedObjectOutputStream;
import me.noip.chankyin.mrmrg.utils.io.SavedProperty;

@EqualsAndHashCode
@ToString
public class TestObjectSave{
	@SavedProperty(1)
	private String testObjectSave = "a";

	@SavedObject(2)
	@NoArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class Foo extends TestObjectSave{
		@SavedProperty(2) private String foo = "b";
		private int i = 0xdeadbeef;
	}

	@SavedObject(3)
	@NoArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class Bar extends Foo{
		@SavedProperty(value = 2, removed = 3) private String bar = "c";
		@SavedProperty(3) private long i = 0xd3adc0debeeffaceL;
	}

	@Test
	public void testWrite() throws Exception{
		testWrite0(new Bar());
	}

	@Test
	public void testRead() throws Exception{
		Bar bar = new Bar();
		testWrite0(bar);
		Object object = testRead0();
		Assert.assertEquals(bar, object);
	}

	public void testWrite0(Object object) throws Exception{
		@Cleanup OutputStream os = new FileOutputStream(new File(".", "Bar.dat"));
		SavedObjectOutputStream soos = new SavedObjectOutputStream(os);
		soos.writeSavedObject(object);
	}

	public Object testRead0() throws Exception{
		@Cleanup InputStream is = new FileInputStream(new File(".", "Bar.dat"));
		SavedObjectInputStream sois = new SavedObjectInputStream(is, true);
		return sois.readSavedObject(null);
	}
}
