package me.noip.chankyin.mrmrg.ui.disp;

public abstract class SmallPrint{
	public static class GeneralInfo{
		public MouseCoords mouseCoords;

		@Override
		public String toString(){
			StringBuilder builder = new StringBuilder();
			if(mouseCoords != null){
				builder.append(mouseCoords.toString());
			}
			return builder.toString();
		}
	}

	@Override
	public String toString(){
		return super.toString();
	}

	public static class MouseCoords extends SmallPrint{
		public double x;
		public double y;

		@Override
		public String toString(){
			return "X = " + x + "\nY = " + y;
		}
	}
}
