package net.minecraft.util.math;
public class BlockPos {
    private final int x,y,z;
    public BlockPos(double x,double y,double z){this.x=(int)x;this.y=(int)y;this.z=(int)z;}
    public BlockPos(int x,int y,int z){this.x=x;this.y=y;this.z=z;}
    public int getX(){return x;} public int getY(){return y;} public int getZ(){return z;}
    public BlockPos down(){ return new BlockPos(x,y-1,z); }
}
