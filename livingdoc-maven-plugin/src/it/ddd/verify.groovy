import org.apache.commons.io.FileUtils

new File(basedir, "./src/test/resources/expected").eachFileMatch(~/^.*\.(adoc|html|plantuml|svg)$/) { file ->
    println "Assert generated file " + file.name + " to exists"

    assert new File(basedir, "target/" + file.name).isFile()
}

new File(basedir, "./src/test/resources/expected").eachFileMatch(~/^.*\.(adoc|plantuml)$/) { expected ->
    println "Assert generated file to equals expected: " + expected.name

    def actual = new File(basedir, "target/" + expected.name)
    assert FileUtils.contentEqualsIgnoreEOL(actual, expected, null)
}
