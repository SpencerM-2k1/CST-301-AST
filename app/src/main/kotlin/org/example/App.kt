/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package org.example

import JavaLexer
import JavaParser 
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.*

import java.io.FileInputStream
import java.io.FileNotFoundException

import java.nio.file.Paths
import java.nio.file.Path

class JavaPrintableTree(val tree: ParseTree) {
    
    override fun toString() = treeString(tree, "")

    private fun treeString(node: ParseTree, prefix: String): String {
        if(node is JavaParser.OrdinaryCompilationUnitContext && node.childCount == 1)
            return visitPrimary(node)
        
        if (node is TerminalNode) return visitTerminal(node)
        if (node !is RuleNode) return "ERROR"

        //is RuleNode
        val name = JavaParser.ruleNames[node.ruleContext.ruleIndex]
        val builder = StringBuilder(name)

        for (i in 0..(node.childCount - 1)) {
            val atEnd = (i == node.childCount - 1)
            val symbol = if(atEnd) "└──" else "├──"

            val child = node.getChild(i)
            val childSymbol = if(atEnd) " " else "|"
            val childStr = treeString(child,  "$prefix$childSymbol   ")

            builder.append("\n$prefix$symbol $childStr")
        }
        
        return "$builder"
    }

    //private fun visitPrimary(node: JavaParser.OrdinaryCompilationUnitContext): String {
    private fun visitPrimary(node: JavaParser.OrdinaryCompilationUnitContext): String {
        val name = JavaParser.ruleNames[node.ruleContext.ruleIndex]
        val childStr = visitTerminal(node.getChild(0) as TerminalNode)
        return "$name ── $childStr"
    }

    private fun visitTerminal(node: TerminalNode): String {
        //println(node.symbol.type - 1)
        //println(node.getStart().getType())
        //if(node.getStart().getType() < 1) return "P'$node'"
        if(node.symbol.type < 1) return "P'$node'"

        val id = JavaParser.ruleNames[node.symbol.type - 1].let { if("T__" in it) "P" else it }

        return "$id'$node'"
    }
}

fun main() {
    
    try {
        //val inputStream = CharStreams.fromString(input)
        //val inputFile = File("Sample.java") 
        val currentPath = Paths.get(".").normalize().toAbsolutePath();
        val filePath = Paths.get(currentPath.toString(), "src", "main", "assets", "Sample.java");

        //println(filePath)
        val fileStream = FileInputStream(filePath.toString());
        val inputStream = CharStreams.fromStream(fileStream)

        val lexer = JavaLexer(inputStream)
        val tokens = CommonTokenStream(lexer)

        val parser = JavaParser(tokens)
        val tree = parser.compilationUnit()

        val printableTree = JavaPrintableTree(tree)
        println(printableTree)
    }
    catch(err: FileNotFoundException) {
        println("File not found!")
        println("File location: " + System.getProperty("user.dir") + "/src/main/assets/");
    }
    catch(err: ArrayIndexOutOfBoundsException) {
        err.printStackTrace(System.out)
    }
}