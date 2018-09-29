package scaberry.oldmacros.impl

import scaberry.core.CopyableField
import scaberry.oldmacros.Meta

class MetaImpl[Source](val fieldsMap: Map[Symbol, CopyableField[Source, _]]) extends Meta[Source]