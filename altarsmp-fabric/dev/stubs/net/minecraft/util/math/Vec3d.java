package net.minecraft.util.math;
public class Vec3d {
    public final double x,y,z;
    public Vec3d(double x,double y,double z){this.x=x;this.y=y;this.z=z;}
    public double getX(){return x;} public double getY(){return y;} public double getZ(){return z;}
    public Vec3d normalize(){ double l=Math.sqrt(x*x+y*y+z*z); return l==0?new Vec3d(0,0,0):new Vec3d(x/l,y/l,z/l); }
}
