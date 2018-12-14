package scaberry

package object core {

  type Getter[-Source, +Type] = Source => Type

  type Copier[Source, -Type] = (Source, Type) => Source

}
