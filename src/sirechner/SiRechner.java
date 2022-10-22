package sirechner;

import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class SiRechner {

  final static String SI_KG = "kg";
  final static String SI_METER = "m";
  final static String SI_SEKUNDE = "s";
  final static String SI_AMPERE = "A";
  final static String SI_KELVIN = "K";
  final static String SI_CANDELA = "cd";
  final static String SI_MOL = "mol";

  final static Unit NEWTON = new Unit("N", List.of(SI_KG, SI_METER), List.of(SI_SEKUNDE, SI_SEKUNDE));
  final static Unit PASCAL = new Unit("Pa", List.of(SI_KG), List.of(SI_METER, SI_SEKUNDE, SI_SEKUNDE));
  final static Unit VOLT = new Unit("V", List.of(SI_KG, SI_METER, SI_METER), List.of(SI_AMPERE, SI_SEKUNDE, SI_SEKUNDE, SI_SEKUNDE));
  final static Unit JOULE = new Unit("J", List.of(SI_KG, SI_METER, SI_METER), List.of(SI_SEKUNDE, SI_SEKUNDE));
  final static Unit COULOMB = new Unit("C", List.of(SI_AMPERE, SI_SEKUNDE), List.of());
  final static Unit FARAD = new Unit("F", List.of(SI_SEKUNDE, SI_SEKUNDE, SI_SEKUNDE, SI_SEKUNDE, SI_AMPERE, SI_AMPERE),
      List.of(SI_KG, SI_METER, SI_METER));

  final static Unit METER = new Unit("m", List.of(SI_METER), List.of());

  final static Formula EPSILON0 = new Formula(new Unit[]{FARAD}, new Unit[]{METER});

  // 8.8541878128(13)×10−12 	F x m^−1
  // 1 F = 1 kg^−1 x m^−2 x s^4 x A^2

  public static void main(String[] args) {
    System.out.println(NEWTON);
    System.out.println(PASCAL);
    System.out.println(VOLT);
    System.out.println(JOULE);
    System.out.println(COULOMB);

    Formula volt = new Formula(new Unit[]{JOULE}, new Unit[]{COULOMB});
    System.out.println(volt.convertToSi("Volt"));

    Formula eins = new Formula(new Unit[]{JOULE}, new Unit[]{JOULE});
    System.out.println(eins.convertToSi("eins").reduce());

    final Unit epsilon = EPSILON0.convertToSi("ε").reduce();
    System.out.println(epsilon);

    Unit force = new Formula(new Unit[] {COULOMB, COULOMB}, new Unit[] {epsilon, METER, METER})
        .convertToSi("Force"); // .reduce();
    System.out.println(force + " (unreduziert)");
    System.out.println(force.reduce() + " (reduziert)");
  }


  public static class Formula {
    final Unit[] numerator;
    final Unit[] denumerator;

    public Formula(Unit[] numerator, Unit[] denumerator) {
      this.numerator = numerator;
      this.denumerator = denumerator;
    }

    public Unit convertToSi(String resultName) {
      List<String> siNumerator = new ArrayList<>();
      List<String> siDenumerator = new ArrayList<>();

      // J * V * W / Pa

      for(Unit num : numerator) {
        List<String> unitNum = num.numerator;
        siNumerator.addAll(unitNum);
        List<String> unitDenum = num.denumerator;
        siDenumerator.addAll(unitDenum);
      }

      for(Unit denum : denumerator) {
        List<String> unitNum = denum.numerator;
        siDenumerator.addAll(unitNum);
        List<String> unitDenum = denum.denumerator;
        siNumerator.addAll(unitDenum);
      }

      return new Unit(resultName + ": ", siNumerator, siDenumerator);
    }
  }

  public static class Unit {

    final String symbol;
    final List<String> numerator;
    final List<String> denumerator;

    public Unit(String symbol, List<String> numerator, List<String> denumerator) {
      this.symbol = symbol;
      this.numerator = numerator;
      this.denumerator = denumerator;
    }

    public Unit reduce() {
      List<String> num = new ArrayList<>();
      List<String> denum = new ArrayList<>(this.denumerator);

      for(String n: this.numerator) {
        if (denum.contains(n)) { // muss gekürzt werden
          // nicht in den num übernehmen.
          denum.remove(n);
        } else { // muss nicht gekuerzt werden
          num.add(n); // wird in den nummerator uebernommen
          // ist im denumerator gar nicht drin, also nix.
        }
      }

      return new Unit(this.symbol, num, denum);
    }

    @Override
    public String toString() {
      return symbol + " = " + numerator.stream().collect(Collectors.joining(" * "))
          + " / "
          + denumerator.stream().collect(Collectors.joining(" * "));
    }
  }
}
