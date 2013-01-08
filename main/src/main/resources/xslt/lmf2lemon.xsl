<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : lmf2lemon.xsl
    Created on : 09 August 2012, 16:40
    Author     : John McCrae
    Description: LMF to Lemon Transforms
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:lemon="http://www.monnet-project.eu/lemon#"
                xmlns:lexinfo="http://www.lexinfo.net/ontology/2.0/lexinfo#"
                xmlns:dcterms="http://purl.org/dc/terms/"
                version="1.0">
    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
        <rdf:RDF>
            <xsl:apply-templates select="LexicalResource"/>
        </rdf:RDF>
    </xsl:template>
    
    <xsl:template match="LexicalResource">
        <xsl:apply-templates select="GlobalInformation"/>
        <xsl:apply-templates select="Lexicon"/>
    </xsl:template>
    
    <xsl:template match="GlobalInformation">
        <rdf:Description rdf:about="#GlobalInformation">
            <xsl:apply-templates select="feat"/>
        </rdf:Description>
    </xsl:template>
    
    <xsl:template match="Lexicon">
        <lemon:Lexicon>
            <xsl:if test="@id">
                <xsl:attribute name="rdf:ID" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="feat/@att='language'">
                <lemon:language>
                    <xsl:value-of select="feat[@att='language']/@val"/>
                </lemon:language>
            </xsl:if>
            <xsl:apply-templates select="feat"/>
            <lemon:entry>
                <xsl:apply-templates select="LexicalEntry"/>
            </lemon:entry>
        </lemon:Lexicon>
        <xsl:apply-templates select="SubcategorizationFrame"/>
        <xsl:apply-templates select="MorphologicalPattern"/>
        <xsl:apply-templates select="ConstraintSet"/>
        <xsl:apply-templates select="MWEPattern"/>
        <xsl:apply-templates select="SemanticPredicate"/>
        <xsl:apply-templates select="SynSemCorrespondence"/>
    </xsl:template>
    
    <xsl:template match="LexicalEntry">
        <lemon:LexicalEntry>
            <xsl:if test="@id">
                <xsl:attribute name="rdf:ID" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="feat"/>
            <xsl:apply-templates select="Lemma"/>
            <xsl:apply-templates select="WordForm[position() > 1]"/>
            <xsl:apply-templates select="SyntacticBehaviour"/>
            <xsl:apply-templates select="Sense"/>
            <xsl:apply-templates select="ListOfComponents"/>
            <xsl:if test="@morphologicalPatterns">
                <lemon:pattern>
                    <xsl:attribute name="rdf:resource">
                        <xsl:value-of select="concat('#',@morphologicalPatterns)"/>
                    </xsl:attribute> 
                </lemon:pattern>
            </xsl:if>
            <xsl:if test="@mwePattern">
                <lemon:phraseRoot>
                    <xsl:attribute name="rdf:resource">
                        <xsl:value-of select="concat('#',@mwePattern)"/>
                    </xsl:attribute>
                </lemon:phraseRoot>
            </xsl:if>
        </lemon:LexicalEntry>
    </xsl:template>
    
    <xsl:template match="Lemma">
        <xsl:if test="feat/@att='writtenForm' and ../WordForm">
            <lemon:canonicalForm>
                <lemon:Form>
                    <xsl:apply-templates select="feat"/>
                    <xsl:apply-templates select="../WordForm[1]/feat"/>
                    <xsl:if test="@id">
                        <xsl:attribute name="rdf:ID" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                            <xsl:value-of select="@id"/>
                        </xsl:attribute>
                    </xsl:if>
                    <lemon:writtenRep>
                        <xsl:if test="FormRepresentation/@xml:lang">
                            <xsl:attribute name="xml:lang">
                                <xsl:value-of select="FormRepresentation/@xml:lang"/>
                            </xsl:attribute>
                        </xsl:if>    
                        <xsl:value-of select="feat/@val"/>
                    </lemon:writtenRep>
                </lemon:Form>
            </lemon:canonicalForm>
        </xsl:if>
        <xsl:if test="feat/@att='writtenForm' and not(../WordForm)">
            <lemon:canonicalForm>
                <lemon:Form>
                    <xsl:apply-templates select="feat"/>
                    <xsl:if test="@id">
                        <xsl:attribute name="rdf:ID" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                            <xsl:value-of select="@id"/>
                        </xsl:attribute>
                    </xsl:if>
                    <lemon:writtenRep>
                        <xsl:if test="FormRepresentation/@xml:lang">
                            <xsl:attribute name="xml:lang">
                                <xsl:value-of select="FormRepresentation/@xml:lang"/>
                            </xsl:attribute>
                        </xsl:if>    
                        <xsl:value-of select="feat/@val"/>
                    </lemon:writtenRep>
                </lemon:Form>
            </lemon:canonicalForm>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="WordForm">
        <xsl:if test="feat/@att='writtenForm'">
            <lemon:otherForm>
                <lemon:Form>
                    <xsl:apply-templates select="feat"/>
                    <xsl:if test="@id">
                        <xsl:attribute name="rdf:ID" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                            <xsl:value-of select="@id"/>
                        </xsl:attribute>
                    </xsl:if>
                    <lemon:writtenRep>
                        <xsl:if test="FormRepresentation/@xml:lang">
                            <xsl:attribute name="xml:lang">
                                <xsl:value-of select="FormRepresentation/@xml:lang"/>
                            </xsl:attribute>
                        </xsl:if>    
                        <xsl:value-of select="feat/@val"/>
                    </lemon:writtenRep>
                </lemon:Form>
            </lemon:otherForm>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="SyntacticBehaviour">
        <xsl:if test="@subcategorizationFrameSets">
            <xsl:variable name="sfs">
                <xsl:value-of select="@subcategorizationFrameSets"/>
            </xsl:variable>
            <xsl:apply-templates select="//SubcategorizationFrameSet[@id=$sfs]"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="SubcategorizationFrameSet">
        <xsl:if test="@subcategorizationFrames">
            <xsl:call-template name="subcat-frames">
                <xsl:with-param name="list" select="@subcategorizationFrames"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="subcat-frames">
        <xsl:param name="list"/> 
        <xsl:variable name="newlist" select="concat(normalize-space($list), ' ')" /> 
        <xsl:variable name="first" select="substring-before($newlist, ' ')" /> 
        <xsl:variable name="remaining" select="substring-after($newlist, ' ')" /> 
        <lemon:synBehavior>
            <xsl:attribute name="rdf:resource" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                <xsl:value-of select="concat('#',$first)"/>
            </xsl:attribute>
        </lemon:synBehavior>
        <xsl:if test="$remaining">
            <xsl:call-template name="subcat-frames">
                <xsl:with-param name="list" select="$remaining" /> 
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template match="SubcategorizationFrame">
        <lemon:Frame>
            <xsl:if test="@id">
                <xsl:attribute name="rdf:ID" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="feat"/>
            <xsl:apply-templates select="SyntacticArgument"/>
        </lemon:Frame>
    </xsl:template>
    
    <xsl:template match="SyntacticArgument">
        <xsl:if test="feat[@att='syntacticFunction']/@val">
            <xsl:variable name="synFunc">
                <xsl:value-of select="feat[@att='syntacticFunction']/@val"/>
            </xsl:variable>
            <xsl:element name="{$synFunc}" namespace="http://lexinfo.net/ontology/2.0/lexinfo#">
                <lemon:Argument>
                    <xsl:if test="@id">
                        <xsl:attribute name="rdf:ID" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                            <xsl:value-of select="@id"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:apply-templates select="feat"/>
                    <xsl:apply-templates select="SyntacticArgument"/>
                </lemon:Argument>
            </xsl:element>
        </xsl:if>
        <xsl:if test="not(feat[@att='syntacticFunction']/@val)">
            <lemon:synArg>
                <lemon:Argument>
                    <xsl:if test="@id">
                        <xsl:attribute name="rdf:ID" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                            <xsl:value-of select="@id"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:apply-templates select="feat"/>
                    <xsl:apply-templates select="SyntacticArgument"/>
                </lemon:Argument>
            </lemon:synArg>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="Sense">
        <lemon:sense>
            <lemon:LexicalSense>
                <xsl:if test="@id">
                    <xsl:attribute name="rdf:ID" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                        <xsl:value-of select="@id"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:apply-templates select="feat"/>
                <xsl:apply-templates select="SenseRelation"/>
                <xsl:apply-templates select="Context"/>
            </lemon:LexicalSense>
        </lemon:sense>
    </xsl:template>
    
    <xsl:template match="SenseRelation">
        <xsl:if test="@targets and feat[@att='type']/@val">
            <xsl:call-template name="senserel-targs">
                <xsl:with-param name="list" select="@targets"/>
                <xsl:with-param name="type" select="feat[@att='type']/@val"/>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="@targets and feat[@att='label']/@val">
            <xsl:call-template name="senserel-targs">
                <xsl:with-param name="list" select="@targets"/>
                <xsl:with-param name="type" select="feat[@att='label']/@val"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
    
    <xsl:template name="senserel-targs">
        <xsl:param name="list"/> 
        <xsl:param name="type"/>
        <xsl:variable name="newlist" select="concat(normalize-space($list), ' ')" /> 
        <xsl:variable name="first" select="substring-before($newlist, ' ')" /> 
        <xsl:variable name="remaining" select="substring-after($newlist, ' ')" /> 
        <xsl:element name="{$type}" namespace="http://lexinfo.net/ontology/2.0/lexinfo#">
            <xsl:attribute name="rdf:resource" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                <xsl:value-of select="concat('#',$first)"/>
            </xsl:attribute>
        </xsl:element>
        <xsl:if test="$remaining">
            <xsl:call-template name="senserel-targs">
                <xsl:with-param name="list" select="$remaining" /> 
                <xsl:with-param name="type" select="$type" /> 
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="Context">
        <xsl:if test="feat[@att='sentence']/@val">
            <lexinfo:sentenceContext>
                <xsl:value-of select="feat[@att='sentence']/@val"/>
            </lexinfo:sentenceContext>
        </xsl:if>
    </xsl:template>
    
    
    <xsl:template match="MorphologicalPattern">
        <lemon:MorphPattern>
            <xsl:if test="@id">
                <xsl:attribute name="rdf:ID" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="feat"/>
            <xsl:apply-templates select="TransformSet"/>
        </lemon:MorphPattern>
    </xsl:template>
    
    <xsl:template match="TransformSet">
        <lemon:transform>
            <lemon:MorphTransform>
                <xsl:if test="@id">
                    <xsl:attribute name="rdf:ID" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                        <xsl:value-of select="@id"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:apply-templates select="feat"/>
                <!-- rules shoule be validated -->
                <lemon:rule>
                    <xsl:for-each select="Process">
                        <xsl:choose>
                            <xsl:when test="feat[@att='operator']/@val='addLemma'">~</xsl:when>
                            <xsl:when test="feat[@att='operator']/@val='addAfter'">
                                <xsl:value-of select="feat[@att='stringValue']/@val"/>
                            </xsl:when>
                            <xsl:when test="feat[@att='operator']/@val='removeAfter'">./~</xsl:when>
                        </xsl:choose>
                    </xsl:for-each>
                </lemon:rule>
                <xsl:apply-templates select="GrammaticalFeatures"/>
            </lemon:MorphTransform>
        </lemon:transform>
    </xsl:template>
    
    <xsl:template match="GrammaticalFeatures">
        <lemon:generates>
            <lemon:Prototype>
                <xsl:if test="@id">
                    <xsl:attribute name="rdf:ID" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                        <xsl:value-of select="@id"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:apply-templates select="feat"/>                        
            </lemon:Prototype>
        </lemon:generates>
    </xsl:template>
    
    <xsl:template match="ConstraintSet">
        <xsl:comment>Constraint sets are not mapped. These should be modelled by the category selection.</xsl:comment>
    </xsl:template>
    
    <xsl:template match="SemanticPredicate">
        <xsl:comment>Semantic Predicates are too difficult to map, please do it manually.</xsl:comment>
    </xsl:template>
    
    <xsl:template match="SynSemCorrespondence">
        <xsl:comment>SynSem Correspondences use odd values in examples. No generic mapping please do it manually.</xsl:comment>
    </xsl:template>
    
    <xsl:template match="ListOfComponents">
        <lemon:decomposition rdf:parseType="Collection">
            <xsl:for-each select="Component">
                <lemon:Component>
                    <lemon:element>
                        <xsl:attribute name="rdf:resource">
                            <xsl:value-of select="concat('#',@entry)"/>
                        </xsl:attribute>
                    </lemon:element>
                </lemon:Component>
            </xsl:for-each>
        </lemon:decomposition>
    </xsl:template>
    
    <xsl:template match="MWEPattern">
        <lemon:Node>
            <xsl:if test="@id">
                <xsl:attribute name="rdf:ID" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="feat"/> 
            <xsl:apply-templates select="MWENode[1]/feat"/> 
            <xsl:for-each select="MWENode[1]/MWEEdge">
                <lemon:edge>
                    <xsl:apply-templates select="MWENode"/>
                </lemon:edge>
            </xsl:for-each>
            <xsl:for-each select="MWENode[1]/MWELex">
                <lemon:edge>
                    <lemon:Node>
                        <xsl:if test="@id">
                            <xsl:attribute name="rdf:ID" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                                <xsl:value-of select="@id"/>
                            </xsl:attribute>
                        </xsl:if>
                        <xsl:apply-templates select="feat"/> 
                        <xsl:if test="@entry">
                            <lemon:leaf>
                                <xsl:attribute name="rdf:resource">
                                    <xsl:value-of select="concat('#',@entry)"/>
                                </xsl:attribute>
                            </lemon:leaf>
                        </xsl:if>
                    </lemon:Node>
                </lemon:edge>
            </xsl:for-each>
        </lemon:Node>
    </xsl:template>
    
    <xsl:template match="MWENode">
        <lemon:Node>
            <xsl:if test="@id">
                <xsl:attribute name="rdf:ID" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="feat"/> 
            <xsl:for-each select="MWEEdge">
                <lemon:edge>
                    <xsl:apply-templates select="MWENode"/>
                </lemon:edge>
            </xsl:for-each>
            <xsl:for-each select="MWELex">
                <lemon:edge>
                    <lemon:Node>
                        <xsl:if test="@id">
                            <xsl:attribute name="rdf:ID" namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                                <xsl:value-of select="@id"/>
                            </xsl:attribute>
                        </xsl:if>
                        <xsl:apply-templates select="feat"/> 
                        <xsl:if test="@entry">
                            <lemon:leaf>
                                <xsl:attribute name="rdf:resource">
                                    <xsl:value-of select="concat('#',@entry)"/>
                                </xsl:attribute>
                            </lemon:leaf>
                        </xsl:if>
                    </lemon:Node>
                </lemon:edge>
            </xsl:for-each>
        </lemon:Node>
    </xsl:template>
    
    <xsl:template match="feat">
        <xsl:if test="@att='partOfSpeech'">
            <lexinfo:partOfSpeech>
                <xsl:attribute name="rdf:resource">
                    <xsl:value-of select="concat('http://www.lexinfo.net/ontology/2.0/lexinfo#',@val)"/>
                </xsl:attribute>
            </lexinfo:partOfSpeech>
        </xsl:if>
        <xsl:if test="@att='grammaticalNumber'">
            <lexinfo:number>
                <xsl:attribute name="rdf:resource">
                    <xsl:value-of select="concat('http://www.lexinfo.net/ontology/2.0/lexinfo#',@val)"/>
                </xsl:attribute>
            </lexinfo:number>
        </xsl:if>
        <xsl:if test="@att='grammaticalGender'">
            <lexinfo:gender>
                <xsl:attribute name="rdf:resource">
                    <xsl:value-of select="concat('http://www.lexinfo.net/ontology/2.0/lexinfo#',@val)"/>
                </xsl:attribute>
            </lexinfo:gender>
        </xsl:if>
        <xsl:if test="@att='grammaticalTense'">
            <lexinfo:tense>
                <xsl:attribute name="rdf:resource">
                    <xsl:value-of select="concat('http://www.lexinfo.net/ontology/2.0/lexinfo#',@val)"/>
                </xsl:attribute>
            </lexinfo:tense>
        </xsl:if>
        <xsl:if test="@att='verbFormMood'">
            <lexinfo:verbFormMood>
                <xsl:attribute name="rdf:resource">
                    <xsl:value-of select="concat('http://www.lexinfo.net/ontology/2.0/lexinfo#',@val)"/>
                </xsl:attribute>
            </lexinfo:verbFormMood>
        </xsl:if>
        <xsl:if test="@att='image'">
            <lexinfo:image>
                <xsl:attribute name="rdf:resource">
                    <xsl:value-of select="@val"/>
                </xsl:attribute>
            </lexinfo:image>
        </xsl:if>
        <xsl:if test="@att='sound'">
            <lexinfo:sound>
                <xsl:attribute name="rdf:resource">
                    <xsl:value-of select="@val"/>
                </xsl:attribute>
            </lexinfo:sound>
        </xsl:if>
        <xsl:if test="@att='gloss'">
            <lexinfo:gloss>
                <lemon:Defintion>
                    <lemon:value>
                        <xsl:value-of select="@val"/>
                    </lemon:value>
                </lemon:Defintion>
            </lexinfo:gloss>
        </xsl:if>
        <xsl:if test="@att='definition'">
            <lemon:definition>
                <lemon:Defintion>
                    <lemon:value>
                        <xsl:value-of select="@val"/>
                    </lemon:value>
                </lemon:Defintion>
            </lemon:definition>
        </xsl:if>
        <xsl:if test="@att='syntacticConstituent'">
            <lemon:constituent>
                <xsl:value-of select="@val"/>
            </lemon:constituent>
        </xsl:if>
        <xsl:if test="@att='pronounciation'">
            <lexinfo:pronounciation>
                <xsl:value-of select="@val"/>
            </lexinfo:pronounciation>
        </xsl:if>
        <xsl:if test="@att='phoneticForm'">
            <lexinfo:pronounciation>
                <xsl:value-of select="@val"/>
            </lexinfo:pronounciation>
        </xsl:if>
        <xsl:if test="@att='creationDate'">
            <dcterms:created>
                <xsl:value-of select="@val"/>
            </dcterms:created>
        </xsl:if>
        <xsl:if test="@att='domain'">
            <dcterms:subject>
                <xsl:value-of select="@val"/>
            </dcterms:subject>
        </xsl:if>
        <xsl:if test="@att='author'">
            <dcterms:creator>
                <xsl:value-of select="@val"/>
            </dcterms:creator>
        </xsl:if>
        <xsl:if test="@att='label'">
            <rdfs:label>
                <xsl:value-of select="@val"/>
            </rdfs:label>
        </xsl:if>
        <xsl:if test="@att='comment'">
            <rdfs:comment>
                <xsl:value-of select="@val"/>
            </rdfs:comment>
        </xsl:if>
        <xsl:if test="@att='past'">
            <lexinfo:pastTenseForm>
                <lemon:Form>
                    <lemon:writtenRep>
                        <xsl:value-of select="@val"/>
                    </lemon:writtenRep>
                </lemon:Form>
            </lexinfo:pastTenseForm>
        </xsl:if>
        <xsl:if test="@att='rank'">
            <lexinfo:rank><xsl:value-of select="@val"/></lexinfo:rank>
        </xsl:if>
        <xsl:if test="@att='graphicalSeparator'">
            <lemon:separator><xsl:value-of select="@val"/></lemon:separator>
        </xsl:if>
        <xsl:if test="@att='structureHead' and @val='yes'">
            <rdf:type rdf:resource="http://lexinfo.net/ontology/2.0/lexinfo#PhraseHead"/>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
