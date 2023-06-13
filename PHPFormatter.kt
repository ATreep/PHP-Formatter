class PHPFormatter {
    companion object{
        fun formatPhpCode(unformattedCode: String): String {
            val regex = Regex("""("(?:\\.|[^"\\])*")|('(?:\\.|[^'\\])*')|/\*(.|\n|\r)*\*/|//.*|[{]|[}]|;|#.*""".trimMargin())

            val code = regex.replace(unformattedCode) {
                val match = it.value
                when {
                    match.startsWith("//") || match.startsWith("#") -> match
                    match.startsWith("/*") && match.endsWith("*/") ->
                        if (match.indexOf("\n") == -1)
                            "\n/*\n" + match.substring(2, match.length - 2) + "\n*/\n"
                        else
                            match
                    match == "{" -> "\n{\n"
                    match == "}" -> "\n}\n"
                    match == ";" -> ";\n"
                    else -> match
                }
            }


            val lines = code.split("\n")
            val formattedLines = mutableListOf<String>()

            var indentationLevel = 0
            val indentSize = 4

            var isCommentLine = false

            for (line in lines) {
                Regex("(\"(?:\\\\.|[^\"\\\\])*\")|('(?:\\\\.|[^'\\\\])*')|/\\*|\\*/|//.*|#.*").findAll(line).let {
                    for (match in it) {
                        if (match.value == "/*") isCommentLine = true
                        if (match.value == "*/") isCommentLine = false
                    }
                }

                val formattedLine = StringBuilder()

                if (isCommentLine)
                {
                    formattedLine.append(line.trim())
                }
                else
                {
                    val matches = Regex("(\"(?:\\\\.|[^\"\\\\])*\")|('(?:\\\\.|[^'\\\\])*')|//.*|#.*").findAll(line)
                    val segments = matches.map { it.value }.toList()

                    var currentIndex = 0
                    for (segment in segments) {
                        val startIndex = line.indexOf(segment, currentIndex)
                        if (startIndex > currentIndex) {
                            val leadingSpaces = line.substring(currentIndex, startIndex)
                            formattedLine.append(leadingSpaces.replace(Regex("\\s+"), " "))
                        }
                        formattedLine.append(segment)
                        currentIndex = startIndex + segment.length
                    }

                    if (currentIndex < line.length) {
                        val trailingSpaces = line.substring(currentIndex)
                        formattedLine.append(trailingSpaces.replace(Regex("\\s+"), " "))
                    }
                }

                formattedLines.add(formattedLine.toString())

                val trimmedLine = formattedLine.toString().trim()
                if (trimmedLine.isEmpty() && !isCommentLine) {
                    formattedLines.removeAt(formattedLines.lastIndex)
                } else if (trimmedLine.isNotEmpty()) {
                    var indentation = " ".repeat(indentationLevel * indentSize)
                    formattedLines[formattedLines.lastIndex] = "$indentation$trimmedLine"

                    if (trimmedLine == "{") {
                        indentationLevel++
                    } else if (trimmedLine == "}") {
                        if(indentationLevel > 0)
                            indentationLevel--
                        indentation = " ".repeat(indentationLevel * indentSize)
                        formattedLines[formattedLines.lastIndex] = "$indentation$trimmedLine"
                    }
                }
            }

            return formattedLines.joinToString("\n")
        }
    }
}
