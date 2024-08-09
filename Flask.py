from flask import Flask, request, jsonify
from inference_sdk import InferenceHTTPClient
from PIL import Image
import pytesseract

app = Flask(__name__)

client = InferenceHTTPClient(
    api_url="https://detect.roboflow.com",
    api_key="kA6VGgbkmG3ATdnEFcC8"
)

def infer_image(image_path):
    result = client.infer(image_path, model_id="invoiceproject/1")
    return result

pytesseract.pytesseract.tesseract_cmd = r'C:/Program Files/Tesseract-OCR/tesseract.exe'

def extract_text_from_region(image_path, bbox):
    image = Image.open(image_path)
    left = bbox['x'] - (bbox['width'] / 2)
    top = bbox['y'] - (bbox['height'] / 2)
    right = bbox['x'] + (bbox['width'] / 2)
    bottom = bbox['y'] + (bbox['height'] / 2)
    cropped_image = image.crop((left, top, right, bottom))
    extracted_text = pytesseract.image_to_string(cropped_image, lang='eng')
    return extracted_text.strip()

@app.route('/extract', methods=['POST'])
def extract():
    data = request.json
    image_path = data['filePath']
    inference_result = infer_image(image_path)

    # Directly return the list of extracted data
    extracted_data = []
    for prediction in inference_result['predictions']:
        class_name = prediction['class']
        bbox = {
            'x': prediction['x'],
            'y': prediction['y'],
            'width': prediction['width'],
            'height': prediction['height']
        }
        value = extract_text_from_region(image_path, bbox)
        extracted_data.append({'class': class_name, 'value': value})

    # Return the list of extracted data directly as JSON
    return jsonify(extracted_data)

if __name__ == '__main__':
    app.run(port=5000, debug=True)
