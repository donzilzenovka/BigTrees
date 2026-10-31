package karob.bigtrees;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import karob.bigtrees.config.Algorithm;
import karob.bigtrees.config.TreeConfiguration;
import karob.bigtrees.config.defaults.Defaults;
import net.minecraftforge.common.config.Configuration;

public class KTreeCfgTrees 
{
	private static List<TreeConfiguration> treeConfigurations;
	
	private static Map<String, TreeConfiguration> treeNameToConfiguration;
	
	public static List<TreeConfiguration> getTreeConfigurations() {
		return treeConfigurations;
	}
		
	public static void init(File configFile) 
	{
		File[] files = configFile.listFiles();
		if (files == null) {
			throw new IllegalStateException("Cannot read BigTrees tree configuration directory: " + configFile);
		}

		treeConfigurations = new LinkedList<TreeConfiguration>();

		if (files.length == 0) {
			for (TreeConfiguration defaults : Defaults.getDefaults()) {
				File filename = new File(configFile, defaults.getAlgorithm() + ".cfg");
				Configuration config = new Configuration(filename);
				config.load();
				TreeConfiguration tree = newConfiguration(defaults);
				tree.readConfig(config);
				treeConfigurations.add(tree);
				config.save();
			}
		} else {
			for (File file : files) {
				if (!file.isFile() || !file.getName().toLowerCase(java.util.Locale.ROOT).endsWith(".cfg")) {
					continue;
				}
				Configuration config = new Configuration(file);
				try {
					config.load();
					// Read the algorithm before applying defaults. A bare TreeConfiguration
					// has a null name, which crashes Forge's Property.setDefaultValue.
					String algorithmName = config.get("Tree Configuration Settings", "Algorithm",
							"", "Tree generation algorithm").getString().trim();
					Algorithm algorithm = Algorithm.valueOf(algorithmName);
					TreeConfiguration defaults = null;
					for (TreeConfiguration candidate : Defaults.getDefaults()) {
						if (candidate.getAlgorithm() == algorithm) {
							defaults = candidate;
							break;
						}
					}
					if (defaults == null) {
						throw new IllegalArgumentException("Unsupported tree algorithm: " + algorithmName);
					}
					TreeConfiguration tree = newConfiguration(defaults);
					tree.readConfig(config);
					treeConfigurations.add(tree);
				} catch (RuntimeException e) {
					throw new IllegalStateException("Invalid BigTrees tree configuration: " + file.getAbsolutePath(), e);
				}
			}
		}

		mapTreeNameToConfiguration();
	}

	private static TreeConfiguration newConfiguration(TreeConfiguration defaults) {
		try {
			return defaults.getClass().asSubclass(TreeConfiguration.class).newInstance();
		} catch (InstantiationException e) {
			throw new IllegalStateException("Cannot instantiate tree defaults for " + defaults.getAlgorithm(), e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Cannot access tree defaults for " + defaults.getAlgorithm(), e);
		}
	}

	private static void mapTreeNameToConfiguration() {
		treeNameToConfiguration = new HashMap<String, TreeConfiguration>();
		for (TreeConfiguration treeConfiguration : treeConfigurations) {
			treeNameToConfiguration.put(treeConfiguration.getName().toLowerCase(), treeConfiguration);
		}
	}

	public static TreeConfiguration getTreeConfiguration(String treeName) {
		return treeNameToConfiguration.get(treeName.toLowerCase());
	}
}
