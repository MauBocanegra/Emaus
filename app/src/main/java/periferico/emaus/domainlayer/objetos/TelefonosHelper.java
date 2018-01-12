package periferico.emaus.domainlayer.objetos;

import android.support.design.widget.TextInputLayout;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

/**
 * Created by maubocanegra on 15/11/17.
 */

public class TelefonosHelper {

    private TextInputLayout textInputLayoutTelefono;
    private EditText editTextTelefono;
    private Spinner spinnerTipoTelefono;
    private ImageButton imageButton;

    public TelefonosHelper() {
    }

    public TextInputLayout getTextInputLayoutTelefono() {
        return textInputLayoutTelefono;
    }

    public void setTextInputLayoutTelefono(TextInputLayout textInputLayoutTelefono) {
        this.textInputLayoutTelefono = textInputLayoutTelefono;
    }

    public EditText getEditTextTelefono() {
        return editTextTelefono;
    }

    public void setEditTextTelefono(EditText editTextTelefono) {
        this.editTextTelefono = editTextTelefono;
    }

    public Spinner getSpinnerTipoTelefono() {
        return spinnerTipoTelefono;
    }

    public void setSpinnerTipoTelefono(Spinner spinnerTipoTelefono) {
        this.spinnerTipoTelefono = spinnerTipoTelefono;
    }

    public ImageButton getImageButton() {
        return imageButton;
    }

    public void setImageButton(ImageButton imageButton) {
        this.imageButton = imageButton;
    }

    public String getTelefono() {
        return editTextTelefono.getEditableText().toString();
    }

    public void setTelefono(String nombre) {
        editTextTelefono.setText(nombre);
    }

    public int getTipo() {
        return spinnerTipoTelefono.getSelectedItemPosition();
    }

    public void setTipo(int tipo) {
        spinnerTipoTelefono.setSelection(tipo);
    }
}
