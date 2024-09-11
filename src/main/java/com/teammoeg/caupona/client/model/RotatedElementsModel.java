package com.teammoeg.caupona.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.joml.AxisAngle4d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import com.mojang.serialization.JsonOps;
import com.teammoeg.caupona.client.model.RotatedBakedModel.Builder;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

public class RotatedElementsModel implements IUnbakedGeometry<RotatedElementsModel>
{

    private final List<BlockElement> elements;
    private final float degree;
    private final Vector3f axis; 
    public RotatedElementsModel(List<BlockElement> elements,float degree,Vector3f axis) {
		super();
		this.elements = elements;
		this.degree=degree;
		this.axis=axis;
	}

	@Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
    {
		try {
        TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
        
        var renderTypeHint = context.getRenderTypeHint();
        var renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;
        Builder builder = new RotatedBakedModel.Builder(degree,context.useAmbientOcclusion(), context.useBlockLight(), context.isGui3d(),
                context.getTransforms(), overrides).particle(particle);

        addQuads(context, builder, baker, spriteGetter, modelState);

        return builder.build(renderTypes);
		}catch(Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
    }

    protected void addQuads(IGeometryBakingContext context, Builder builder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState)
    {
        // If there is a root transform, undo the ModelState transform, apply it, then re-apply the ModelState transform.
        // This is necessary because of things like UV locking, which should only respond to the ModelState, and as such
        // that is the only transform that should be applied during face bake.

        for(int i=0;i<360/degree;i++) {
            var postTransform = context.getRootTransform().isIdentity() ? QuadTransformers.applying(new Transformation(null,null,null,new Quaternionf(new AxisAngle4d(i*degree/180*Math.PI,axis)))) :
                QuadTransformers.applying(modelState.getRotation().compose(context.getRootTransform()).compose(modelState.getRotation().inverse())
                	.compose(new Transformation(null,null,null,new Quaternionf(new AxisAngle4d(i*degree/180*Math.PI,axis)))));
        	for (BlockElement element : elements)
            {
                for (Direction direction : element.faces.keySet())
                {
                    BlockElementFace face = element.faces.get(direction);
                    TextureAtlasSprite sprite = spriteGetter.apply(context.getMaterial(face.texture()));
                    BakedQuad quad = BlockModel.bakeFace(element, face, sprite, direction, modelState);
                    postTransform.processInPlace(quad);
                    builder.addUnculledFace(quad,i);
                }
            }
        }
        
    }
    
    public static final class Loader implements IGeometryLoader<RotatedElementsModel>
    {
        public Loader(){
        }
        @Override
        public RotatedElementsModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException
        {
            if (!jsonObject.has("elements"))
                throw new JsonParseException("An element model must have an \"elements\" member.");

            List<BlockElement> elements = new ArrayList<>();
            int degree=jsonObject.get("degree").getAsInt();
            Vector3f axis=ExtraCodecs.VECTOR3F.decode(JsonOps.INSTANCE, jsonObject.get("axis")).getOrThrow().getFirst();
            //groups.forEach((k,v)->System.out.print(k+":"+String.join(",",v.stream().map(String::valueOf).toList())));
            for (JsonElement element : GsonHelper.getAsJsonArray(jsonObject, "elements"))
            {
                elements.add(deserializationContext.deserialize(element, BlockElement.class));
            }

            return new RotatedElementsModel(elements,degree,axis);
        }
    }


}