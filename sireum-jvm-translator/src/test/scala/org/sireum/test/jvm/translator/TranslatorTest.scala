package org.sireum.test.jvm.translator

import org.junit.Assert._
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.sireum.jvm.translator.ClassTranslator
import org.sireum.pilar.parser.PilarParser
import org.scalatest.junit.JUnitRunner
import org.sireum.jvm.util.Util
import org.sireum.pilar.parser.ChunkingPilarParser
import org.sireum.pilar.symbol._
import org.sireum.util._
import org.sireum.pilar.ast._
import org.sireum.alir.ControlFlowGraph
import org.sireum.alir.AlirIntraProceduralGraph
import java.io.PrintWriter
import scala.collection.immutable.Stack
import java.io.StringWriter

@RunWith(classOf[JUnitRunner])
class TranslatorTest extends FunSuite {

  test("It should parse") {
    val reporter = new PilarParser.StringErrorReporter(true)
    BytecodeTranslator.main(Array("java.lang.String"))  
    val ms = ChunkingPilarParser(Right("file:/Users/Vidit/Dropbox/Classes/Spring2013/FinalProject/Sireum2Workspace/sireum-translator/sireum-jvm-translator/output"), reporter)
//    val fst = {_:Unit => new ST }
//    val ast = SymbolTable.apply(List(ms.get), fst, false)
//    val pl : AlirIntraProceduralGraph.NodePool = mmapEmpty
//    val cfgs = ast.procedureSymbolTables map (pst => ControlFlowGraph[String](pst, "Entry", "Exit", pl, { (_: LocationDecl,_:Iterable[CatchClause]) => (Array.empty[CatchClause], false) }))
//    val dotOutput = new StringWriter
//    cfgs foreach (cfg => cfg.toDot(new PrintWriter(dotOutput)))
//    
//    val printer = new PrintWriter(new java.io.File("cfg.dot"))
//    printer.write(dotOutput.toString())
//    printer.close()
    //PilarParser.apply(Right("file:/Users/Vidit/Dropbox/AndroidStuff/classes.pilar"), reporter)
    assertTrue(reporter.errorAsString, reporter.errorAsString.isEmpty())
    //Map
    //new java.util.HashMap[String, String]
  }
    
   class ST extends SymbolTable with SymbolTableProducer {
    st =>

    import PilarSymbolResolverModuleDef.ERROR_TAG_TYPE
    import PilarSymbolResolverModuleDef.WARNING_TAG_TYPE

    val tables = SymbolTableData()
    val tags = marrayEmpty[LocationTag]
    var hasErrors = false

    def reportError(source : Option[FileResourceUri], line : Int,
                    column : Int, message : String) : Unit = {
      tags += Tag.toTag(source, line, column, message, ERROR_TAG_TYPE)
      hasErrors = true
    }

    def reportWarning(fileUri : Option[String], line : Int,
                      column : Int, message : String) : Unit =
      tags += Tag.toTag(fileUri, line, column, message, WARNING_TAG_TYPE)

    val pdMap = mmapEmpty[ResourceUri, PST]

    def globalVars = tables.globalVarTable.keys
    def globalVar(globalUri : ResourceUri) = tables.globalVarTable(globalUri)

    def procedures = tables.procedureTable.keys

    def procedures(procedureUri : ResourceUri) = tables.procedureTable(procedureUri)

    def procedureSymbolTables = pdMap.values

    def procedureSymbolTable(procedureAbsUri : ResourceUri) : ProcedureSymbolTable =
      procedureSymbolTableProducer(procedureAbsUri)

    def procedureSymbolTableProducer(procedureAbsUri : ResourceUri) = {
      assert(tables.procedureAbsTable.contains(procedureAbsUri))
      pdMap.getOrElseUpdate(procedureAbsUri, new PST(procedureAbsUri))
    }

    class PST(val procedureUri : ResourceUri)
        extends ProcedureSymbolTable with ProcedureSymbolTableProducer {
      val tables = ProcedureSymbolTableData()
      var nextLocTable : CMap[ResourceUri, ResourceUri] = null
      def symbolTable = st
      def symbolTableProducer = st
      def procedure = st.tables.procedureAbsTable(procedureUri)
      def typeVars : ISeq[ResourceUri] = tables.typeVarTable.keys.toList
      def params : ISeq[ResourceUri] = tables.params.toList
      def isParam(localUri : ResourceUri) = tables.params.contains(localUri)
      def locals : Iterable[ResourceUri] = tables.localVarTable.keys
      def nonParamLocals : Iterable[ResourceUri] = tables.localVarTable.keys.filterNot(isParam)
      def locations =
        tables.bodyTables match {
          case Some(bt) => procedure.body.asInstanceOf[ImplementedBody].locations
          case _        => ivectorEmpty
        }
      def typeVar(typeVarUri : ResourceUri) : NameDefinition =
        tables.typeVarTable(typeVarUri)
      def param(paramUri : ResourceUri) : ParamDecl =
        tables.localVarTable(paramUri).asInstanceOf[ParamDecl]
      def local(localUri : ResourceUri) : LocalVarDecl =
        tables.localVarTable(localUri).asInstanceOf[LocalVarDecl]
      def location(locationIndex : Int) = locations(locationIndex)
      def location(locationUri : ResourceUri) =
        tables.bodyTables.get.locationTable(locationUri)
      def catchClauses(locationIndex : Int) : Iterable[CatchClause] =
        tables.bodyTables.get.catchTable.getOrElse(locationIndex,
          Array.empty[CatchClause] : Iterable[CatchClause])
    }

    def toSymbolTable : SymbolTable = this
  }
   

}

  
object PilarSymbolResolverModuleDef {
  val ERROR_TAG_TYPE = MarkerType(
    "org.sireum.pilar.tag.error.symtab",
    None,
    "Pilar Symbol Resolution Error",
    MarkerTagSeverity.Error,
    MarkerTagPriority.Normal,
    ivector(MarkerTagKind.Problem, MarkerTagKind.Text))
  val WARNING_TAG_TYPE = MarkerType(
    "org.sireum.pilar.tag.error.symtab",
    None,
    "Pilar Symbol Resolution Warning",
    MarkerTagSeverity.Warning,
    MarkerTagPriority.Normal,
    ivector(MarkerTagKind.Problem, MarkerTagKind.Text))
}
// <@Frame ("new", 5, `[int, int, int, (|java.lang.Object|)], 0)>
// <@Frame (@New, 9, `[(|java.lang.String|), int, int, int, int, long, (|java.lang.Object|), (|int|), int, int, :top, :top], 0, `[:top, :top, :top, :top])>;
// <@Frame (@New, 9, `[(|java.lang.String|), int, int, int, int, long, (|java.lang.Object|), (|int|), int, int, :top, :top], 0, `[:top, :top, :top, :top])>