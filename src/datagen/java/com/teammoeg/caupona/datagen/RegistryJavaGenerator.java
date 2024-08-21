package com.teammoeg.caupona.datagen;

import java.util.HashMap;
import java.util.Map;

import com.teammoeg.caupona.CPFluids;
import com.teammoeg.caupona.CPMain;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class RegistryJavaGenerator extends FileGenerator {

	public RegistryJavaGenerator(PackOutput output, ExistingFileHelper helper) {
		super(PackType.SERVER_DATA, output, helper,"Caupona Registry Java");
	}
	
	
	@Override
	protected void gather(FileStorage reciver) {
		JavaFileOutput fo=this.createGeneratedJavaOutput("CPStewTexture");
		fo.addImport(HashMap.class);
		fo.addImport(Map.class);
		fo.addImportDelimeter();
		fo.addImport(ResourceLocation.class);
		fo.createMap("public static","texture",HashMap.class,String.class,ResourceLocation.class);
		fo.defineBlock("static");
		for(String sf:CPFluids.getSoupfluids()) {
			ResourceLocation image = ResourceLocation.fromNamespaceAndPath(CPMain.MODID, "textures/block/soups/" + sf + ".png");
			if (helper.exists(image, PackType.CLIENT_RESOURCES)) {
				fo.line().call("texture.put")
					.paramString(sf)
					.paramCall("ResourceLocation.fromNamespaceAndPath")
						.paramString(CPMain.MODID)
						.paramString("block/soups/"+sf)
					.complete()
				.complete().end();
			}
		}
		reciver.accept(fo.complete());
		
	}

}
