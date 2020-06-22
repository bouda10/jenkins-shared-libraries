package org.test
@Grab('org.yaml:snakeyaml:1.17')
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.DumperOptions
import static org.yaml.snakeyaml.DumperOptions.FlowStyle.BLOCK

class yamlinjector implements Serializable {
 def  updateDeployment(String fileName, String imageName){
    def yaml = readYaml file: $fileName
    yaml.spec.template.image = $imageName
    writeFile file:$fileName, text:yamlToString(yaml)
}
}