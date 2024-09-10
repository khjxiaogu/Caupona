/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona;

import java.util.ArrayList;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;


public class CPConfig {

	public static void register() {
		// ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT,
		// Config.CLIENT_CONFIG);
		ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, CPConfig.COMMON_CONFIG);
		ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.SERVER, CPConfig.SERVER_CONFIG);
	}

	public static class Client {
		public ConfigValue<Boolean> fancyPan;
		/**
		 * @param builder
		 */
		Client(ModConfigSpec.Builder builder) {
		}
	}

	public static class Common {
		//public ConfigValue<Integer> staticTime;
		public ConfigValue<Double> roadSpeedAddtion;
		public ConfigValue<Integer> loafCooking;
		public ConfigValue<Integer> loafStacking;
		/**
		 * @param builder
		 */

		Common(ModConfigSpec.Builder builder) {
			builder.push("recipe");
			/*staticTime = builder.comment("Ticks before do still recipe").defineInRange("StillRecipeTicks", 12000, 1,
					Integer.MAX_VALUE);*/
			loafCooking = builder.comment("Loaf dough cooking time").defineInRange("loafCookingTime",
				1200, 1, Integer.MAX_VALUE);
			loafStacking = builder.comment("Loaf dough maximum stacking height in blocks").defineInRange("maxLoafStackingHeight",
				6, 1, Integer.MAX_VALUE);
			builder.pop();
			builder.push("road");
			roadSpeedAddtion = builder.comment("Additional speed added per tick for roads").defineInRange("roadSpeedAddtion",2D,0D,10D);
			builder.pop();
		}
	}

	public static class Server {
		/**
		 * @param builder
		 */
		public ConfigValue<Integer> chimneyTicks;
		public ConfigValue<Integer> chimneyCheck;
		public ConfigValue<Integer> chimneyStorage;
		public ConfigValue<Integer> stoveCD;
		public ConfigValue<Integer> fumaroleSpeed;
		public ConfigValue<Integer> fumaroleCheck;
		public ConfigValue<Integer> fumarolePower;
		public ConfigValue<Double> stoveFuel;
		public ConfigValue<Integer> potCookTimeBase;
		public ConfigValue<Integer> potMixTimeBase;
		public ConfigValue<Integer> fryTimeBase;
		public ConfigValue<Integer> containerTick;
		public ConfigValue<Integer> bathExp;
		public ConfigValue<Double> bathChance;
		public ConfigValue<Integer> bathPath;
		public ConfigValue<Integer> wolfTick;
		public ConfigValue<Integer> bathRange;
		public ConfigValue<Boolean> genCH;
		public ConfigValue<Boolean> strictWater;

		public ConfigValue<Double> benefitialMod;
		public ConfigValue<Double> harmfulMod;

		public ConfigValue<Double> leadenGenRate;

		public ConfigValue<Boolean> addManual;
		Server(ModConfigSpec.Builder builder) {
			builder.push("recipes");

			potCookTimeBase = builder.comment("Stew pot cooking mininum time in ticks").defineInRange("potCookMinTicks",
					100, 1, Integer.MAX_VALUE);
			potMixTimeBase = builder.comment("Stew pot mixture mininum time in ticks").defineInRange("potMixMinTicks",
					50, 1, Integer.MAX_VALUE);
			fryTimeBase = builder.comment("Pan frying mininum time in ticks").defineInRange("fryMinTicks", 100, 1,
					Integer.MAX_VALUE);
			containerTick = builder.comment("Tick interval between container input check").defineInRange("containTick",
					10, 1, Integer.MAX_VALUE);
			
			builder.pop();

			builder.push("chimney");
			chimneyTicks = builder.comment("How many ticks does a chimney pot needed to make a soot")
					.define("ChimneySootTicks", 80);
			chimneyCheck = builder.comment("Interval in ticks for a chimney to check it's validity")
					.defineInRange("ChimneyCheckTicks", 20, 1, Integer.MAX_VALUE);
			chimneyStorage = builder.comment("Max soot stored in a chimney").defineInRange("ChimneySootStorage", 8, 1,
					64);
			builder.pop();

			builder.push("stoves");
			stoveCD = builder.comment("How many ticks should the stove pause burning when work is done")
					.define("StovePauseTimer", 100);
			stoveFuel = builder.comment("Stove fuel value multiplier").define("StoveFuelMultiplier", 1.0);
			builder.pop();

			builder.push("fumarole");
			fumaroleSpeed = builder.comment("Interval in ticks for a  fumarole vent to generate pumice bloom")
					.defineInRange("FumaroleTicks", 100, 1, Integer.MAX_VALUE);
			fumaroleCheck = builder.comment("Interval in ticks for a fumarole vent to check its heat source")
					.defineInRange("FumaroleCheckTicks", 20, 1, Integer.MAX_VALUE);
			fumarolePower = builder.comment("Fumarole heat value, set to 0 to disable fumarole heat.")
					.defineInRange("FumaroleHeat", 1, 0, Integer.MAX_VALUE);
			builder.pop();

			builder.push("hypocast");
			builder.comment("Actual expectation of experience per tick is change x exp");
			bathExp = builder.comment("How many exp add to bathing play when conditions meet, set 0 to disable")
					.defineInRange("BathExperience", 1, 0, Integer.MAX_VALUE);
			bathChance = builder.comment("The chance add the exp to player per tick per caliduct block")
					.defineInRange("BathGetExpChance", 0.005f, 0f, 1f);
			bathPath = builder.comment("Interval for bath heat check").defineInRange("BathHeatTicks", 20, 1,
					Integer.MAX_VALUE);
			wolfTick = builder.comment("Interval for wolf statue heat check").defineInRange("WolfTicks", 10, 1,
					Integer.MAX_VALUE);
			bathRange = builder.comment("Firebox heat conduct radius").defineInRange("FireboxRadius", 4, 0,
					Integer.MAX_VALUE);
			strictWater = builder.comment("Strict player in water check, player must be in water to get bonus.").define("StrictInWaterCheck",true);
			builder.pop();

			builder.push("misc");
			genCH = builder.comment("Super secret special content").define("specialContents", true);
			addManual=builder.comment("Add manual to player on start").define("addManual", true);
			leadenGenRate=builder.comment("Leaden walnut tranformation rate").defineInRange("leadenGenRate",0.05D,0D,1D);
			builder.pop();
			builder.push("compat");
			builder.push("diet");
			builder.comment("You would only need to modify this when diet mod installed, otherwist this does not take effect");
			benefitialMod=builder.comment("Benefitial diet value modifier for cooking food into stew").define("benefitialModifier",1.2);
			harmfulMod=builder.comment("Harmful diet value modifier for cooking food into stew").define("harmfulModifier",0.8);
			builder.pop();
			builder.pop();
		}
	}

	// public static final ForgeConfigSpec CLIENT_CONFIG;
	public static final ModConfigSpec COMMON_CONFIG;
	public static final ModConfigSpec SERVER_CONFIG;
	// public static final Client CLIENT;
	public static final Common COMMON;
	public static final Server SERVER;

	public static ArrayList<String> DEFAULT_WHITELIST = new ArrayList<>();

	static {
		// ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
		// CLIENT = new Client(CLIENT_BUILDER);
		// CLIENT_CONFIG = CLIENT_BUILDER.build();
		ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
		COMMON = new Common(COMMON_BUILDER);
		COMMON_CONFIG = COMMON_BUILDER.build();
		ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
		SERVER = new Server(SERVER_BUILDER);
		SERVER_CONFIG = SERVER_BUILDER.build();
	}
}
