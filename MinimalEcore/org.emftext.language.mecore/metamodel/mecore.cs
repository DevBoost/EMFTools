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
	DEFINE UPPER $('A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'.'|'_')*$;
	DEFINE FRAGMENT LOWER_CHAR $('a'..'z')$;
	DEFINE LOWER LOWER_CHAR + $('a'..'z'|'A'..'Z'|'0'..'9'|'.'|'_')*$;
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
	MPackage ::= annotations* (name[LOWER] #1)? namespace[URI] 
				 (!0 imports)*
				 (!0 annotationDefinitions)* 
				 !0 
				 (!0 contents)*;
				 
	MImport  ::= "import" #1 importedPackage[URI] #1 "as" prefix[LOWER];


	MAnnotationDefinition ::= "define" "@"name[LOWER] "as" source['\'','\''];
	 
	MAnnotation ::= "@"annotationDefinition[LOWER] entries*;
	
	MAnnotationEntry ::= key['\'','\''] "=" value['\'','\''];

	@SuppressWarnings(optionalKeyword)
	MClass   ::= comment[ML_COMMENT]?
				 annotations*
				 abstract["abstract" : ""] 
				 interface["interface" : ""]
	             name[UPPER]
	             ("<" typeParameters ("," typeParameters)* ">")? 
	             (":" superTypeReferences ("," superTypeReferences)* )? 
	             (#1 "(" (!1 features)* (!1 operations)* !0 ")")? !0;

	MTypeReference ::= 
				(type[UPPER] | eType[LOWER]) 
				("<" typeArguments ("," typeArguments)* ">")?;
				
	MTypeParameter ::= name[UPPER] (":" lowerBound)?;
	
	@SuppressWarnings(featureWithoutSyntax, optionalKeyword)
	MEnum    ::= annotations* "enum" name[UPPER] ("(" literals* ")")?;
	MEnumLiteral ::= annotations* name[UPPER] literal['"','"']?;
	
	MFeature ::= annotations* ncReference["~" : ""] name[LOWER] type multiplicity? ("<>" opposite[UPPER])?;
	
	MOperation ::= annotations*  
		("<" typeParameters ("," typeParameters)* ">")?
		name[LOWER] 
		"(" (parameters ("," parameters)*)? ")" type multiplicity?;
		
	MParameter ::= name[LOWER] type
		("<" typeArguments ("," typeArguments)* ">")? 
		multiplicity?;
	
	MSimpleMultiplicity ::= value[star : "*", optional : "?", plus : "+"];
	MComplexMultiplicity ::= "(" lowerBound[INTEGER] ".." upperBound[INTEGER] ")";
	
	MWildcard ::= "?";
}