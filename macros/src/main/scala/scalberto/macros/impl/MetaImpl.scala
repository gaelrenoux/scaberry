package scalberto.macros.impl

import scalberto.core.CopyableField
import scalberto.macros.Meta

class MetaImpl[Source](val fieldsMap: Map[Symbol, CopyableField[Source, _]]) extends Meta[Source]