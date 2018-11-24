package scaberry.macrostests.models

import scaberry.macros.berry

/** Case class with more than 22 fields */
@berry
case class Alphabet(
    alpha: String, beta: String, gamma: String, delta: String, epsilon: String, zeta: String, eta: String,
    theta: String, iota: String, kappa: String, la: String, mu: String, nu: String, xi: String, omicron: String,
    pi: String, rho: String, sigma: String, tau: String, upsilon: String, phi: String, chi: String, psi: String,
    omega: String
  )