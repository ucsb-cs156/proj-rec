import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import RequestTypeDropdown from '../../../main/components/RequestTypeDropdown/RequestTypeDropdown';

describe('RequestTypeDropdown Component', () => {
  const options = [
    { value: 'GET', label: 'GET Request' },
    { value: 'POST', label: 'POST Request' },
    { value: 'PUT', label: 'PUT Request' },
    { value: 'DELETE', label: 'DELETE Request' },
  ];

  test('renders without crashing', () => {
    render(<RequestTypeDropdown options={options} onChange={() => {}} />);
    const dropdown = screen.getByRole('combobox');
    expect(dropdown).toBeInTheDocument();
  });

  test('renders all options correctly', () => {
    render(<RequestTypeDropdown options={options} onChange={() => {}} />);
    fireEvent.mouseDown(screen.getByRole('combobox'));
    options.forEach(option => {
      expect(screen.getByText(option.label)).toBeInTheDocument();
    });
  });

  test('calls onChange when an option is selected', () => {
    const handleChange = jest.fn();
    render(<RequestTypeDropdown options={options} onChange={handleChange} />);
    const dropdown = screen.getByRole('combobox');
    fireEvent.mouseDown(dropdown);
    fireEvent.click(screen.getByText('POST Request'));
    expect(handleChange).toHaveBeenCalledWith('POST');
  });

  test('displays the correct selected value', () => {
    render(<RequestTypeDropdown options={options} value="PUT" onChange={() => {}} />);
    const dropdown = screen.getByRole('combobox');
    expect(dropdown.value).toBe('PUT');
  });
});
