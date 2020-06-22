@Grab('org.yaml:snakeyaml:1.17')
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.DumperOptions
import static org.yaml.snakeyaml.DumperOptions.FlowStyle.BLOCK
def call(def fileName, def imageName) {
dir('hello-world-war/') {

sh 'ls -l'
    def yaml = readYaml(file: "${fileName}")
    yaml.spec.template.image = "${imageName}"
    writeFile file:"${fileName}", text:(yaml.toString())
}
}