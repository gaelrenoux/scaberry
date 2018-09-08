package scalberto.macros.impl

import scalberto.core.Field
import scalberto.core.Field.Copier
import scalberto.macros.Meta

class MetaImpl[Source](val fieldsMap: Map[Symbol, Field[Source, _, Copier[Source, _]]]) extends Meta[Source]