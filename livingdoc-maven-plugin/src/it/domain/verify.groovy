new File(basedir, ".").eachFileMatch(~/^.*\.(adoc|html|plantuml)$/) { file ->
    println "Assert generated file " + file.name + " to exists"

    assert new File(basedir, "target/generated-docs/" + file.name).isFile()
}

new File(basedir, ".").eachFileMatch(~/^.*\.(adoc|plantuml)$/) { file ->
    println "Assert generated file to equals expected: " + file.name

    assert new File(basedir, "target/generated-docs/" + file.name).text == file.text
}

