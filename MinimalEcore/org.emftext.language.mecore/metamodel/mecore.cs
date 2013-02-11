@SuppressWarnings(tokenOverlapping,noRuleForMetaClass,minOccurenceMismatch,explicitSyntaxChoice)
SYNTAXDEF mecore
FOR <http://www.emftext.org/language/mecore> <mecore.genmodel>
START MPackage

OPTIONS {
	licenceHeader ="../../org.dropsbox/licence.txt";
	
	usePredefinedTokens = "false";
	overrideBuilder = "false";
	disableLaunchSupport = "true";
	disableDebugSupport = "true";
	
	additionalDependencies = "org.eclipse.emf.codegen.ecore,org.eclipse.emf.ecore.xmi";
}

TOKENS {
	DEFINE INTEGER $('-')?('0'..'9')+$;
	DEFINE UPPER $('A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'.')*$;
	DEFINE FRAGMENT LOWER_CHAR $('a'..'z')$;
	DEFINE LOWER LOWER_CHAR + $('a'..'z'|'A'..'Z'|'0'..'9'|'.')*$;
	DEFINE WHITESPACES $(' '|'\t'|'\f')+$;
	DEFINE LINEBREAKS $('\r'|'\n')+$;
	DEFINE COMMENT $'//'(~('\n'|'\r'))*$;
	DEFINE ML_COMMENT $'/*'.*'*/'$;
	DEFINE URI $'<' ($ + LOWER_CHAR + $|'.') ('a'..'z'|'0'..'9'|'A'..'Z'|'.'|':'|'/'|'_'|'%'|'-'|'?'|'&'|'='|'#')+'>'$;
}

TOKENSTYLES {
	"UPPER" COLOR #000000, BOLD;
	"abstract" COLOR #800040, BOLD;
	"COMMENT" COLOR #404040;
	"URI", "<>" COLOR #0080C0, BOLD;
} 

RULES {
	MPackage ::= (name[LOWER] #1)? namespace[URI] 
				 (!0 imports)* 
				 !0 
				 (!0 contents)*;
				 
	MImport  ::= "import" #1 importedPackage[URI] #1 "as" prefix[LOWER];

	@SuppressWarnings(optionalKeyword)
	MClass   ::= comment[ML_COMMENT]?
				 abstract["abstract" : ""] 
				 interface["interface" : ""]
	             name[UPPER]
	             ("<" typeParameters ("," typeParameters)* ">")? 
	             (":" superTypeReferences ("," superTypeReferences)* )? 
	             (#1 "(" (!1 features)* (!1 operations)* !0 ")")? !0;

	MSuperTypeReference ::= 
				(supertype[UPPER] | eSupertype[LOWER]) 
				("<" typeArguments[UPPER] ("," typeArguments[UPPER])* ">")?;
				
	MTypeParameter ::= name[UPPER] (":" lowerBound[UPPER])?;
	
	@SuppressWarnings(featureWithoutSyntax, optionalKeyword)
	MEnum    ::= "enum" name[UPPER] ("(" literals* ")")?;
	MEnumLiteral ::= name[UPPER] literal['"','"']?;
	
	MFeature ::= ncReference["~" : ""] name[LOWER] (type[UPPER]|type[LOWER]) multiplicity? ("<>" opposite[UPPER])?;
	
	MOperation ::=  
		("<" typeParameters ("," typeParameters)* ">")?
		name[LOWER] 
		"(" (parameters ("," parameters)*)? ")" (type[UPPER]|type[LOWER]) multiplicity?;
		
	MParameter ::= name[LOWER] (type[UPPER]|type[LOWER])
		("<" typeArguments[UPPER] ("," typeArguments[UPPER])* ">")? 
		multiplicity?;
	
	MSimpleMultiplicity ::= value[star : "*", optional : "?", plus : "+"];
	MComplexMultiplicity ::= "(" lowerBound[INTEGER] ".." upperBound[INTEGER] ")";
}