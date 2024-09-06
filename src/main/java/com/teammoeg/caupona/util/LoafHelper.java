package com.teammoeg.caupona.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.teammoeg.caupona.CPTags.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class LoafHelper {
	private static final Vec3i[] horizontal_vec=new Vec3i[] {
		new Vec3i(1,0,0),new Vec3i(0,0,1),new Vec3i(-1,0,0),new Vec3i(0,0,-1)
	};
	public static Vec3i[][] generateBounds(int radius){
		Vec3i[][] directions=new Vec3i[(radius*2+1)*(radius*2+1)][];
		for(int x=-radius;x<=radius;x++)
			for(int y=-radius;y<=radius;y++) {
				List<Vec3i> vec3s=new ArrayList<>();
				for(Vec3i v3i:horizontal_vec) {
					if(Math.abs(x+v3i.getX())<radius&&Math.abs(y+v3i.getY())<radius)
						vec3s.add(v3i);
				}
				directions[encodeXY(radius,x,y)]=vec3s.toArray(Vec3i[]::new);
			}
		return directions;
	}
	private static final Vec3i[][] bound_table=generateBounds(1);
	public static int encodeXY(int radius,int x,int y) {
		return(x+radius)+(y+radius)*3;
	}
	private LoafHelper() {
	}
	public static float getFireStrengh(Level l,BlockPos origin) {
		Set<Vec3i> origins=new HashSet<>(9);
		Set<Vec3i> current=new HashSet<>(9);
		origins.add(Vec3i.ZERO);
		float heat_factor=0;
		for(int currentLayer=0;currentLayer>=-6;currentLayer--) {
			for(Vec3i p:origins) {
				Vec3i[] neighbors=bound_table[encodeXY(1,p.getX(),p.getY())];
				current.clear();
				for(Vec3i neighbor:neighbors) {
					BlockState state=l.getBlockState(origin.offset(neighbor.getX(), currentLayer, neighbor.getZ()));
					if(state.isEmpty()||state.is(Blocks.LOAF_HEATING_IGNORE))
						current.add(neighbor);
					else if(state.is(Blocks.LOAF_HEATING_BLOCKS))
						heat_factor++;	
				}
			}
			Set<Vec3i> temp=origins;
			origins=current;
			current=temp;
		}
		return heat_factor;
	}
	public static boolean isPlaceValid(BlockPos pos,AABB bound) {
		return  pos.getX()>bound.minX&&
				pos.getY()>bound.minY&&
				pos.getZ()>bound.minZ&&
				pos.getX()<bound.maxX&&
				pos.getY()<bound.maxY&&
				pos.getZ()<bound.maxZ;
	}
}
