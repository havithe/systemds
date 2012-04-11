package dml.runtime.instructions.CPInstructions;

import dml.parser.Expression.ValueType;
import dml.runtime.controlprogram.ProgramBlock;
import dml.runtime.functionobjects.Divide;
import dml.runtime.functionobjects.Power;
import dml.runtime.matrix.operators.BinaryOperator;
import dml.runtime.matrix.operators.Operator;
import dml.utils.DMLRuntimeException;

public class ScalarScalarArithmeticCPInstruction extends ArithmeticBinaryCPInstruction{
	public ScalarScalarArithmeticCPInstruction(Operator op, 
								   CPOperand in1, 
								   CPOperand in2,
								   CPOperand out, 
								   String istr){
		super(op, in1, in2, out, istr);
	}
	
	@Override
	public ScalarObject processInstruction(ProgramBlock pb) throws DMLRuntimeException{
		// 1) Obtain data objects associated with inputs 
		ScalarObject so1 = pb.getScalarVariable(input1.get_name(), input1.get_valueType());
		ScalarObject so2 = pb.getScalarVariable(input2.get_name(), input2.get_valueType() );
		ScalarObject sores = null;
		
		
		// 2) Compute the result value & make an appropriate data object 
		BinaryOperator dop = (BinaryOperator) optr;
		
		if ( input1.get_valueType() == ValueType.STRING 
			 || input2.get_valueType() == ValueType.STRING ) {
			String rval = dop.fn.execute(so1.getStringValue(), so2.getStringValue());
			sores = (ScalarObject) new StringObject(rval);
		}
		else if ( input1.get_valueType() == ValueType.INT && input2.get_valueType() == ValueType.INT ) {
			// TODO: statiko: can we remove the use of instanceof ?
			if ( dop.fn instanceof Divide || dop.fn instanceof Power ) {
				// If both inputs are of type INT then output must be an INT if operation is not divide or power
				double rval = dop.fn.execute ( so1.getIntValue(), so2.getIntValue() );
				sores = (ScalarObject) new DoubleObject(rval);
			}
			else {
				// If both inputs are of type INT then output must be an INT if operation is not divide or power
				int rval = (int) dop.fn.execute ( so1.getIntValue(), so2.getIntValue() );
				sores = (ScalarObject) new IntObject(rval);
			}
		}
		
		else {
			// If either of the input is of type DOUBLE then output is a DOUBLE
			double rval = dop.fn.execute ( so1.getDoubleValue(), so2.getDoubleValue() );
			sores = (ScalarObject) new DoubleObject(rval); 
		}
		
		// 3) Put the result value into ProgramBlock
		pb.setVariable(output.get_name(), sores);
		return sores;
		
	}
}
